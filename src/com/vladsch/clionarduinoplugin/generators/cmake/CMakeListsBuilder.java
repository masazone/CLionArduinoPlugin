package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.generators.cmake.ast.Argument;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.Command;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeElement;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeText;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for creating, reading and modifying CMakeLists.txt
 * <p>
 * A spec CMake parser but only intended for Arduino Support created CMakeLists.txt files:
 * <p>
 * cmake_minimum_required(VERSION 2.8.4)
 * set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
 * set(PROJECT_NAME tft_life)
 * <p>
 * ## This must be set before project call
 * set(${CMAKE_PROJECT_NAME}_BOARD pro)
 * set(ARDUINO_CPU 8MHzatmega328)
 * <p>
 * project(${PROJECT_NAME})
 * <p>
 * # Define the source code
 * set(${PROJECT_NAME}_SRCS tft_life.cpp)
 * #set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
 * link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)
 * <p>
 * #### Uncomment below additional settings as needed.
 * set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
 * set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
 * set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
 * # set(pro.upload.speed 57600)
 * <p>
 * generate_arduino_firmware(${CMAKE_PROJECT_NAME})
 */
public class CMakeListsBuilder {

    // commands can have fixed and variable arguments
    // fixed arguments can have dependency on variable arguments of other commands in the command set for a given cmake file
    // this allows fixed args to be dependent on args of other commands or values in the command set
    //
    // <$COMMAND_NAME$> refers to variable arg 0 of command with name COMMAND_NAME or string mapped by COMMAND_NAME
    // <$COMMAND_NAME[]$> refers to variable arg 0 of command with name COMMAND_NAME
    // <$COMMAND_NAME[2]$> refers to variable arg 2 of command with name COMMAND_NAME
    //
    // if index is invalid then it is the same as if 0 was used.
    //
    // if command is not found or has less args then an empty value will be used
    //
    final static public CMakeCommandType CMAKE_MINIMUM_REQUIRED = new CMakeCommandType("CMAKE_MINIMUM_REQUIRED", "cmake_minimum_required", new String[] { "VERSION" }, 1, 1);
    final static public CMakeCommandType LINK_DIRECTORIES = new CMakeCommandType("LINK_DIRECTORIES", "link_directories", new String[0], 1, 100, true);
    final static public CMakeCommandType PROJECT = new CMakeCommandType("PROJECT", "project", new String[] { "${PROJECT_NAME}" }, 0, 1);

    final static public CMakeCommandType GENERATE_ARDUINO_FIRMWARE = new CMakeCommandType("GENERATE_ARDUINO_FIRMWARE", "generate_arduino_firmware", new String[] { "${CMAKE_PROJECT_NAME}" }, 0, 0);
    final static public CMakeCommandType GENERATE_ARDUINO_LIBRARY = new CMakeCommandType("GENERATE_ARDUINO_LIBRARY", "generate_arduino_library", new String[] { "${CMAKE_PROJECT_NAME}" }, 0, 0);

    final static public CMakeCommandType SET = new CMakeCommandType("SET", "set", new String[] { }, 2, 1000);

    // @formatter:off
    final static public CMakeCommandType SET_CMAKE_TOOLCHAIN_FILE   = new CMakeCommandType("SET_CMAKE_TOOLCHAIN_FILE"   , "set", new String[] { "CMAKE_TOOLCHAIN_FILE"              , }, 1, 1, false, new String[]{"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"});
    final static public CMakeCommandType SET_PROJECT_NAME           = new CMakeCommandType("SET_PROJECT_NAME"           , "set", new String[] { "PROJECT_NAME" }                    , 1, 1);
    final static public CMakeCommandType SET_BOARD                  = new CMakeCommandType("SET_BOARD"                  , "set", new String[] { "${CMAKE_PROJECT_NAME}_BOARD" }     , 1, 1);
    final static public CMakeCommandType SET_CPU                    = new CMakeCommandType("SET_CPU"                    , "set", new String[] { "ARDUINO_CPU" }                     , 1, 1);
    final static public CMakeCommandType SET_SKETCH                 = new CMakeCommandType("SET_SKETCH"                 , "set", new String[] { "${CMAKE_PROJECT_NAME}_SKETCH" }    , 1, 1);
    final static public CMakeCommandType SET_PROGRAMMER             = new CMakeCommandType("SET_PROGRAMMER"             , "set", new String[] { "${CMAKE_PROJECT_NAME}_PROGRAMMER" }, 1, 1);
    final static public CMakeCommandType SET_PORT                   = new CMakeCommandType("SET_PORT"                   , "set", new String[] { "${CMAKE_PROJECT_NAME}_PORT" }      , 1, 1);
    final static public CMakeCommandType SET_AFLAGS                 = new CMakeCommandType("SET_AFLAGS"                 , "set", new String[] { "${CMAKE_PROJECT_NAME}_AFLAGS" }    , 1, 1000);
    final static public CMakeCommandType SET_HDRS                   = new CMakeCommandType("SET_HDRS"                   , "set", new String[] { "${PROJECT_NAME}_HDRS" }      , 1, 1000, true);
    final static public CMakeCommandType SET_SRCS                   = new CMakeCommandType("SET_SRCS"                   , "set", new String[] { "${PROJECT_NAME}_SRCS" }      , 1, 1000, true);
    // @formatter:on

    // with variables that need to be provided
    final static public CMakeCommandType SET_LIB_NAME_RECURSE = new CMakeCommandType("SET_LIB_NAME_RECURSE", "set", new String[] { "<$LIB_NAME$>_RECURSE" }, 1, 1);
    final static public CMakeCommandType SET_UPLOAD_SPEED = new CMakeCommandType("SET_UPLOAD_SPEED", "set", new String[] { "<$SET_BOARD$>.upload.speed" }, 1, 1);

    final static Pattern COMMAND_REF = Pattern.compile("<\\$([a-zA-Z_$][a-zA-Z_0-9$]*)(?:\\[(\\d+)\\])?\\$>");

    final static public CMakeCommandType[] ourCommandTypeList = new CMakeCommandType[] {
            CMAKE_MINIMUM_REQUIRED, LINK_DIRECTORIES, PROJECT, GENERATE_ARDUINO_FIRMWARE, GENERATE_ARDUINO_LIBRARY, SET,
            SET_CMAKE_TOOLCHAIN_FILE, SET_PROJECT_NAME, SET_BOARD, SET_CPU, SET_SKETCH, SET_PROGRAMMER, SET_PORT, SET_AFLAGS, SET_HDRS, SET_SRCS, SET_LIB_NAME_RECURSE, SET_UPLOAD_SPEED
    };

    final static public HashMap<String, CMakeCommandType> ourCommands = new HashMap<String, CMakeCommandType>();
    final static public HashMap<String, CMakeCommandType> ourSetCommands = new HashMap<String, CMakeCommandType>();
    final static public HashMap<String, CMakeCommandType> ourSetCommandsArg0 = new HashMap<String, CMakeCommandType>();
    final static public HashMap<String, CMakeCommandType> ourCMakeCommands = new HashMap<String, CMakeCommandType>();
    static {
        for (CMakeCommandType commandType : ourCommandTypeList) {
            if ("set".equals(commandType.getCommand()) && commandType.getFixedArgs().length > 0) {
                ourSetCommands.put(commandType.getName(), commandType);
                ourSetCommandsArg0.put(commandType.getFixedArgs()[0], commandType);
            } else {
                ourCommands.put(commandType.getName(), commandType);
                ourCMakeCommands.put(commandType.getCommand(), commandType);
            }
        }
    }
    public static CMakeElement elementFrom(@NotNull Node node, @Nullable Map<String, Object> valueSet) {
        // either command or text
        if (node instanceof Command) {
            // find the command or we can create a new one if we don't have it already or just make it into a text block
            Command command = (Command) node;
            CMakeCommandType commandType = null;
            ArrayList<String> commandArgs = new ArrayList<>();

            if (command.getCommand().equals("set")) {
                // see if we have a matching set command
                Argument arg = (Argument) command.getFirstChildAny(Argument.class);
                if (arg != null) {
                    String setCommand = arg.getText().toString();

                    commandType = ourSetCommandsArg0.get(setCommand);
                    if (commandType != null) {
                        // TODO: check all fixed arguments not just the first one

                    } else {
                        // if the name contains a macro then it won't be expanded but the setCommand is expanded
                        // need to convert it by expanding dependent names
                        if (valueSet != null) {
                            for (String name : ourSetCommandsArg0.keySet()) {
                                String converted = replacedCommandParams(name, valueSet);
                                if (converted.equals(setCommand)) {
                                    // it is this
                                    commandType = ourSetCommandsArg0.get(name);
                                }
                            }
                        }
                    }
                }
            }

            if (commandType == null) {
                commandType = ourCMakeCommands.get(command.getCommand().toString());
            }

            if (commandType != null) {
                CMakeCommand makeCommand = new CMakeCommand(commandType, false);
                int skipArgs = commandType.getFixedArgs().length;

                int i = 0;
                for (Node arg : node.getChildren()) {
                    if (arg instanceof Argument && i >= skipArgs) {
                        makeCommand.setArg(i - skipArgs, ((Argument) arg).getText().toString());
                    }
                    i++;
                }

                return makeCommand;
            }
        }

        // create a text element
        return new CMakeText(node.getChars().toString(), false);
    }

    static public String replacedCommandParams(@NotNull String arg, Map<String, Object> valueSet) {
        Matcher matcher = COMMAND_REF.matcher(arg);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            do {
                String commandRef = matcher.group(1);
                Object ref = valueSet.get(commandRef);
                String value = "";

                if (ref != null) {
                    if (ref instanceof CMakeCommand) {
                        CMakeCommand command = (CMakeCommand) ref;
                        int index = 0;
                        value = matcher.group(2);
                        if (value != null) {
                            try {
                                index = Integer.parseUnsignedInt(value);
                            } catch (NumberFormatException ignored) {
                            }
                        }

                        value = index < command.getArgCount() ? command.getArg(index) : "";
                    } else {
                        value = ref.toString();
                    }
                }

                matcher.appendReplacement(sb, value);
            } while (matcher.find());

            matcher.appendTail(sb);
            return sb.toString();
        }
        return arg;
    }

    final private @NotNull ArrayList<CMakeElement> myElements = new ArrayList<>();
    private @Nullable CMakeFile myCMakeFile = null;
    final private HashMap<CMakeElement, Node> myElementNodeMap = new HashMap<>();

    public CMakeListsBuilder() {

    }

    public CMakeListsBuilder(@NotNull CharSequence text, @Nullable DataHolder options) {
        this(text, options, null);
    }

    public CMakeListsBuilder(@NotNull CharSequence text, @Nullable DataHolder options, @Nullable Map<String, Object> values) {
        HashMap<String, Object> valueSet = new HashMap<>();
        if (values != null) valueSet.putAll(values);

        CMakeParser parser = new CMakeParser(BasedSequenceImpl.of(text), options);
        myCMakeFile = parser.getDocument();

        // now load the commands
        for (Node node : myCMakeFile.getChildren()) {
            CMakeElement element = elementFrom(node, valueSet);
            addElement(element, node);

            if (element instanceof CMakeCommand) {
                String typeName = ((CMakeCommand) element).getCommandType().getName();
                valueSet.put(typeName, element);
            }
        }
    }

    public CMakeListsBuilder(@NotNull CMakeFile cMakeFile) {
        this(cMakeFile, null);
    }

    public CMakeListsBuilder(@NotNull CMakeFile cMakeFile, @Nullable Map<String, Object> values) {
        HashMap<String, Object> valueSet = new HashMap<>();
        if (values != null) valueSet.putAll(values);

        myCMakeFile = cMakeFile;

        // now load the commands
        for (Node node : myCMakeFile.getChildren()) {
            CMakeElement element = elementFrom(node, valueSet);
            addElement(element, node);

            if (element instanceof CMakeCommand) {
                String typeName = ((CMakeCommand) element).getCommandType().getName();
                valueSet.put(typeName, element);
            }
        }
    }

    public @NotNull String getCMakeContents(@Nullable Map<String, Object> values) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, Object> valueSet = new HashMap<>();
        if (values != null) valueSet.putAll(values);

        for (CMakeElement element : myElements) {
            if (element instanceof CMakeCommand) {
                String typeName = ((CMakeCommand) element).getCommandType().getName();
                if (!valueSet.containsKey(typeName)) {
                    valueSet.put(typeName, element);
                }
            }
        }

        for (CMakeElement element : myElements) {
            try {
                element.appendTo(sb, valueSet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sb.length() > 0) sb.append("\n");
        return sb.toString();
    }

    public @Nullable CMakeCommand getSetCommand(String name) {
        CMakeCommandType commandType = ourSetCommands.get(name);
        if (commandType == null) {
            commandType = ourCommands.get(name);
        }

        if (commandType != null) {
            for (CMakeElement element : myElements) {
                if (element instanceof CMakeCommand) {
                    if (((CMakeCommand) element).getCommandType() == commandType) {
                        return (CMakeCommand) element;
                    }
                }
            }
        }
        return null;
    }

    public @NotNull List<CMakeElement> getElements() {
        return myElements;
    }

    public void addElement(@NotNull CMakeElement element) {
        myElements.add(element);
    }

    public void addElement(@NotNull CMakeElement element, Node node) {
        myElementNodeMap.put(element, node);
        myElements.add(element);
    }

    public void addElement(int index, @NotNull CMakeElement element) {
        myElements.add(index, element);
    }

    public void setElement(int index, @NotNull CMakeElement element) {
        myElements.set(index, element);
    }

    public void removeElement(int index) {
        myElements.remove(index);
    }

    public void removeElement(@NotNull CMakeElement element) {
        myElements.remove(element);
    }
}
