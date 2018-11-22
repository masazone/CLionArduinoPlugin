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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for creating, reading and modifying CMakeLists.txt files with specific flavour
 * provided by specialized subclasses
 * <p>
 * This class knows the how and when of manipulating the file but no knowledge of what
 */
public abstract class CMakeListsTxtBuilder {
    // commands can have fixed and variable arguments
    // fixed arguments can have dependency on variable arguments of other commands in the command set for a given cmake file
    // this allows fixed args to be dependent on args of other commands or values in the command set
    //
    //
    public static final int INF_MAX_ARGS = 1000;
    final static Pattern COMMAND_REF = Pattern.compile("<\\$([a-zA-Z_$][a-zA-Z_0-9$]*)(?:\\[(\\d+)\\])?\\$>");

    final private @NotNull ArrayList<CMakeElement> myElements = new ArrayList<>();
    private @Nullable CMakeFile myCMakeFile = null;
    final private HashMap<CMakeElement, Node> myElementNodeMap = new HashMap<>();
    final Map<String, CMakeCommandType> myCMakeCommands;
    final Map<String, CMakeCommandType> myCommands;
    final Map<String, CMakeCommandType> mySetCommands;
    final Map<String, CMakeCommandType> mySetCommandsArg0;

    public CMakeListsTxtBuilder(CMakeCommandType[] commands) {
        myCMakeCommands = new HashMap<>();
        myCommands = new HashMap<>();
        mySetCommands = new HashMap<>();
        mySetCommandsArg0 = new HashMap<>();

        for (CMakeCommandType commandType : commands) {
            if ("set".equals(commandType.getCommand()) && commandType.getFixedArgs().length > 0) {
                mySetCommands.put(commandType.getName(), commandType);
                mySetCommandsArg0.put(commandType.getFixedArgs()[0], commandType);
            } else {
                myCommands.put(commandType.getName(), commandType);
                myCMakeCommands.put(commandType.getCommand(), commandType);
            }
        }
    }

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, @NotNull CharSequence text, @Nullable DataHolder options) {
        this(commands, text, options, null);
    }

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, @NotNull CharSequence text, @Nullable DataHolder options, @Nullable Map<String, Object> values) {
        this(commands);

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

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, @NotNull CMakeFile cMakeFile) {
        this(commands, cMakeFile, null);
    }

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, @NotNull CMakeFile cMakeFile, @Nullable Map<String, Object> values) {
        this(commands);

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

    public CMakeElement elementFrom(@NotNull Node node, @Nullable Map<String, Object> valueSet) {
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

                    commandType = mySetCommandsArg0.get(setCommand);
                    if (commandType != null) {
                        // TODO: check all fixed arguments not just the first one

                    } else {
                        // if the name contains a macro then it won't be expanded but the setCommand is expanded
                        // need to convert it by expanding dependent names
                        if (valueSet != null) {
                            for (String name : mySetCommandsArg0.keySet()) {
                                String converted = replacedCommandParams(name, valueSet);
                                if (converted.equals(setCommand)) {
                                    // it is this
                                    commandType = mySetCommandsArg0.get(name);
                                }
                            }
                        }
                    }
                }
            }

            if (commandType == null) {
                commandType = myCMakeCommands.get(command.getCommand().toString());
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

    public @Nullable CMakeCommand getCommand(String name) {
        CMakeCommandType commandType = mySetCommands.get(name);
        if (commandType == null) {
            commandType = myCommands.get(name);
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

    public void addElementAfter(@NotNull CMakeElement anchor, @NotNull CMakeElement element) {
        int index = myElements.indexOf(anchor);
        if (index == -1) {
            // TODO: figure out of error is better but should not happen unless anchor is from another builder
            myElements.add(element);
            return;
        }
        myElements.add(index + 1, element);
    }

    public void addElementBefore(@NotNull CMakeElement anchor, @NotNull CMakeElement element) {
        int index = myElements.indexOf(anchor);
        if (index == -1) {
            // TODO: figure out of error is better but should not happen unless anchor is from another builder
            myElements.add(0, element);
            return;
        }
        myElements.add(index, element);
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

    CMakeCommand addCommand(@NotNull String name, String...args) {
        return addCommand(name, Arrays.asList(args));
    }

    CMakeCommand addCommand(@NotNull String name, @Nullable Collection<String> args) {
        //noinspection unchecked
        List<String> argList = args == null ? Collections.EMPTY_LIST : new ArrayList<>(args);
        CMakeCommand command = getCommand(name);
        CMakeCommand newCommand = command;

        if (command != null) {
            CMakeCommandType commandType = command.getCommandType();

            if (!commandType.isMultiple() && commandType.getMaxArgs() == CMakeListsTxtBuilder.INF_MAX_ARGS) {
                if (myElementNodeMap.get(command) != null) {
                    // original command, replace it with new
                    newCommand = new CMakeCommand(command);
                }
                newCommand.addAll(argList);
                addElementAfter(command, newCommand);
                removeElement(command);
            } else {
                // TODO: make this more generic???
                if (commandType.isMultiple()) {
                    if (commandType.isNoDupeArgs()) {
                        // add another one after this one if the arg value is different
                        if (!command.allArgsEqual(argList)) {
                            newCommand = new CMakeCommand(command);
                            command.setAddEOL(true);
                            addElementAfter(command, newCommand);
                        }
                    } else {
                        // add another one after this one
                        newCommand = new CMakeCommand(command);
                        newCommand.setAll(argList);
                        command.setAddEOL(true);
                        addElementAfter(command, newCommand);
                    }
                } else {
                    if (myElementNodeMap.get(command) != null) {
                        // original command, replace it with new
                        newCommand = new CMakeCommand(command);
                    }
                    newCommand.setAll(argList);
                    addElementAfter(command, newCommand);
                    removeElement(command);
                }
            }
        }
        return newCommand;
    }

    /**
     * replace other commands' argument references in the given string
     *
     * <p>
     * <$COMMAND_NAME$> refers to variable arg 0 of command with name COMMAND_NAME or string mapped by COMMAND_NAME
     * <$COMMAND_NAME[2]$> refers to variable arg 2 of command with name COMMAND_NAME
     * <$COMMAND_NAME[]$> invalid, always empty result
     * <$COMMAND_NAME[-1]$> first from from the end, ie. command.getArgCount() - 1
     * <$COMMAND_NAME[-3]$> third from from the end, ie. command.getArgCount() - 3
     * <p>
     * CAUTION: if index is invalid then it is the same as the value not being empty
     * <p>
     * if command is not found or has less args then an empty value will be used
     *
     * @param arg string with possible command references and variable value references
     * @param valueSet map of names to values, if value is CMakeCommand then its argument value will be extracted, otherwise the argument value will be the String value of passed object
     * @return string with variables replaced
     */
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
                                index = Integer.parseInt(value);
                            } catch (NumberFormatException ignored) {
                                index = command.getArgCount();
                            }
                        }

                        if (index < 0) {
                            index += command.getArgCount();
                        }

                        value = index >= 0 && index < command.getArgCount() ? command.getArg(index) : "";
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
}
