package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType;
import com.vladsch.clionarduinoplugin.resources.Strings;
import com.vladsch.clionarduinoplugin.util.Utils;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.vladsch.clionarduinoplugin.util.Utils.ifNullOrEmpty;

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
public class ArduinoCMakeListsTxtBuilder extends CMakeListsTxtBuilder {
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
    final static public CMakeCommandType LINK_DIRECTORIES = new CMakeCommandType("LINK_DIRECTORIES", "link_directories", new String[0], 1, INF_MAX_ARGS, true, false, true);
    final static public CMakeCommandType ADD_SUBDIRECTORY = new CMakeCommandType("ADD_SUBDIRECTORY", "add_subdirectory", new String[0], 1, 3, false);
    final static public CMakeCommandType PROJECT = new CMakeCommandType("PROJECT", "project", new String[] { "${CMAKE_PROJECT_NAME}" }, 1, INF_MAX_ARGS, true, false, false);
    final static public CMakeCommandType SET = new CMakeCommandType("SET", "set", new String[] { }, 1, INF_MAX_ARGS);

    final static public CMakeCommandType GENERATE_ARDUINO_FIRMWARE = new CMakeCommandType("GENERATE_ARDUINO_FIRMWARE", "generate_arduino_firmware", new String[] { "${CMAKE_PROJECT_NAME}" }, 0, 0, true, false, true);
    final static public CMakeCommandType GENERATE_ARDUINO_LIBRARY = new CMakeCommandType("GENERATE_ARDUINO_LIBRARY", "generate_arduino_library", new String[] { "${CMAKE_PROJECT_NAME}" }, 0, 0, true, false, true);

    // @formatter:off
    final static public CMakeCommandType SET_CMAKE_TOOLCHAIN_FILE   = new CMakeCommandType("SET_CMAKE_TOOLCHAIN_FILE"   , "set", new String[] { "CMAKE_TOOLCHAIN_FILE"              , }, 1, 1, false, false, true, new String[]{"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"});
    final static public CMakeCommandType SET_CMAKE_CXX_STANDARD     = new CMakeCommandType("SET_CMAKE_CXX_STANDARD"     , "set", new String[] { "CMAKE_CXX_STANDARD"                , }, 1, 1, false, false, true);
    final static public CMakeCommandType SET_PROJECT_NAME           = new CMakeCommandType("SET_PROJECT"                , "set", new String[] { "PROJECT_NAME" }                    , 2, 2, false, false, true);
    final static public CMakeCommandType SET_BOARD                  = new CMakeCommandType("SET_BOARD"                  , "set", new String[] { "${CMAKE_PROJECT_NAME}_BOARD" }     , 1, 1, false, false, true);
    final static public CMakeCommandType SET_CPU                    = new CMakeCommandType("SET_CPU"                    , "set", new String[] { "ARDUINO_CPU" }                     , 1, 1, false, false, true);
    final static public CMakeCommandType SET_SKETCH                 = new CMakeCommandType("SET_SKETCH"                 , "set", new String[] { "${CMAKE_PROJECT_NAME}_SKETCH" }    , 1, 1, false, false, true);
    final static public CMakeCommandType SET_PROGRAMMER             = new CMakeCommandType("SET_PROGRAMMER"             , "set", new String[] { "${CMAKE_PROJECT_NAME}_PROGRAMMER" }, 1, 1, false, false, true);
    final static public CMakeCommandType SET_PORT                   = new CMakeCommandType("SET_PORT"                   , "set", new String[] { "${CMAKE_PROJECT_NAME}_PORT" }      , 1, 1, false, false, true);
    final static public CMakeCommandType SET_AFLAGS                 = new CMakeCommandType("SET_AFLAGS"                 , "set", new String[] { "${CMAKE_PROJECT_NAME}_AFLAGS" }    , 1, INF_MAX_ARGS,true, false,true);
    final static public CMakeCommandType SET_HDRS                   = new CMakeCommandType("SET_HDRS"                   , "set", new String[] { "${CMAKE_PROJECT_NAME}_HDRS" }            , 0, INF_MAX_ARGS, true, false,true);
    final static public CMakeCommandType SET_SRCS                   = new CMakeCommandType("SET_SRCS"                   , "set", new String[] { "${CMAKE_PROJECT_NAME}_SRCS" }            , 0, INF_MAX_ARGS, true, false,true);
    // @formatter:on

    // with variables that need to be provided
    final static public CMakeCommandType SET_LIB_NAME_RECURSE = new CMakeCommandType("SET_LIB_NAME_RECURSE", "set", new String[] { "<$LIB_NAME$>_RECURSE" }, 1, 1, false, false, true);
    final static public CMakeCommandType SET_UPLOAD_SPEED = new CMakeCommandType("SET_UPLOAD_SPEED", "set", new String[] { "<$SET_BOARD$>.upload.speed" }, 1, 1, false, false, true);

    final static Pattern COMMAND_REF = Pattern.compile("<\\$([a-zA-Z_$][a-zA-Z_0-9$]*)(?:\\[(\\d+)\\])?\\$>");

    final static public CMakeCommandType[] ourCommands = new CMakeCommandType[] {
            CMAKE_MINIMUM_REQUIRED,
            LINK_DIRECTORIES,
            ADD_SUBDIRECTORY,
            PROJECT,
            GENERATE_ARDUINO_FIRMWARE,
            GENERATE_ARDUINO_LIBRARY,
            SET,

            SET_CMAKE_TOOLCHAIN_FILE,
            SET_CMAKE_CXX_STANDARD,
            SET_PROJECT_NAME,
            SET_BOARD,
            SET_CPU,
            SET_SKETCH,
            SET_PROGRAMMER,
            SET_PORT,
            SET_AFLAGS,
            SET_HDRS,
            SET_SRCS,
            SET_LIB_NAME_RECURSE,
            SET_UPLOAD_SPEED
    };

    final static public CMakeCommandAnchor[] ourAnchors = new CMakeCommandAnchor[] {
            CMakeCommandAnchor.first(CMAKE_MINIMUM_REQUIRED),
            CMakeCommandAnchor.first(SET_CMAKE_TOOLCHAIN_FILE),
            CMakeCommandAnchor.first(SET_CMAKE_CXX_STANDARD),

            CMakeCommandAnchor.before(PROJECT, SET_BOARD),
            CMakeCommandAnchor.before(PROJECT, SET_CPU),

            CMakeCommandAnchor.after(PROJECT, LINK_DIRECTORIES),
            CMakeCommandAnchor.after(PROJECT, ADD_SUBDIRECTORY),
            CMakeCommandAnchor.after(PROJECT, SET_SKETCH),
            CMakeCommandAnchor.after(PROJECT, SET_PROGRAMMER),
            CMakeCommandAnchor.after(PROJECT, SET_PORT),
            CMakeCommandAnchor.after(PROJECT, SET_AFLAGS),
            CMakeCommandAnchor.after(PROJECT, SET_HDRS),
            CMakeCommandAnchor.after(PROJECT, SET_SRCS),
            CMakeCommandAnchor.after(PROJECT, SET_LIB_NAME_RECURSE),
            CMakeCommandAnchor.after(PROJECT, SET_UPLOAD_SPEED),

            CMakeCommandAnchor.last(GENERATE_ARDUINO_FIRMWARE),
            CMakeCommandAnchor.last(GENERATE_ARDUINO_LIBRARY),
    };

    public ArduinoCMakeListsTxtBuilder() {
        super(ourCommands, ourAnchors);
    }

    public ArduinoCMakeListsTxtBuilder(@NotNull final CharSequence text, @Nullable final DataHolder options) {
        super(ourCommands, ourAnchors, text, options);
    }

    public ArduinoCMakeListsTxtBuilder(@NotNull final CharSequence text, @Nullable final DataHolder options, @Nullable final Map<String, Object> values) {
        super(ourCommands, ourAnchors, text, options, values);
    }

    public ArduinoCMakeListsTxtBuilder(@NotNull final CMakeFile cMakeFile) {
        super(ourCommands, ourAnchors, cMakeFile);
    }

    public ArduinoCMakeListsTxtBuilder(@NotNull final CMakeFile cMakeFile, @Nullable final Map<String, Object> values) {
        super(ourCommands, ourAnchors, cMakeFile, values);
    }

    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(CMakeParser.AUTO_CONFIG, true)
            .set(CMakeParser.AST_LINE_END_EOL, true)
            .set(CMakeParser.AST_COMMENTS, true)
            .set(CMakeParser.AST_BLANK_LINES, true)
            .set(CMakeParser.AST_ARGUMENT_SEPARATORS, true);

    public static String getCMakeFileContent(@NotNull String template, @NotNull String projectName, @NotNull ArduinoApplicationSettings mySettings, boolean myIsLibrary, File[] sourceFiles) {
        CMakeCommand command;
        ArduinoCMakeListsTxtBuilder builder = new ArduinoCMakeListsTxtBuilder(template, OPTIONS);
        boolean isStaticLib = myIsLibrary && "static".equals(mySettings.getLibraryType());

        builder.setWantCommentedOut(true);

        builder.setOrAddCommand("cmake_minimum_required", "2.8.4");
        builder.setOrAddCommand("SET_CMAKE_TOOLCHAIN_FILE", "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake");
        builder.setOrAddCommand("SET_CMAKE_CXX_STANDARD", mySettings.getLanguageVersionLineForCMake()).commentOut(mySettings.getLanguageVersion().isEmpty());

        // not needed, we remove it
        command = builder.getCommand("SET_PROJECT");
        if (command != null) {
            command.commentOut(true);
        }

        String boardId = ifNullOrEmpty(mySettings.getBoardId(), "uno");
        builder.setOrAddCommand("SET_BOARD", boardId);

        List<String> cpuIds = mySettings.getArduinoConfig().getBoardById(boardId).getCpuIds();
        String defValue = cpuIds.size() == 0 ? "" : cpuIds.get(0);
        String cpuId = ifNullOrEmpty(mySettings.getCpuId(), defValue);
        builder.setOrAddCommand("SET_CPU", cpuId).commentOut(cpuId.isEmpty());

        builder.setOrAddCommand("PROJECT").clearArgs();

        ArrayList<String> cppFiles = new ArrayList<>();
        ArrayList<String> hFiles = new ArrayList<>();
        String sketchFile = null;

        for (File file : sourceFiles) {
            String ext = Utils.getExtension(file);
            if (ext != null) {
                if (ext.equalsIgnoreCase("c") || ext.equalsIgnoreCase(Strings.CPP_EXT)) {
                    cppFiles.add(file.getName());
                } else if (ext.equalsIgnoreCase("hpp") || ext.equalsIgnoreCase(Strings.H_EXT)) {
                    hFiles.add(file.getName());
                } else if (ext.equalsIgnoreCase(Strings.INO_EXT) || ext.equalsIgnoreCase(Strings.PDE_EXT)) {
                    sketchFile = file.getName();
                }
            }
        }

        builder.setOrAddCommand("SET_SRCS", cppFiles).commentOut(cppFiles.isEmpty());
        builder.setOrAddCommand("SET_HDRS", hFiles).commentOut(hFiles.isEmpty());

        // TODO: implement
        //builder.addCommand("SET_LIBS", libFiles).commentOut(libFiles.isEmpty());

        builder.setOrAddCommand("SET_SKETCH", projectName + Strings.DOT_INO_EXT)
                .commentOut(sketchFile == null);

        builder.setOrAddCommand("LINK_DIRECTORIES", "${CMAKE_CURRENT_SOURCE_DIR}/" + mySettings.getLibraryDirectory())
                .commentOut(!mySettings.isAddLibraryDirectory() || mySettings.getLibraryDirectory().isEmpty());

        if (!myIsLibrary) {
            // TODO: add options for additional libraries and recursion options
            // if (sketchFile != null) {
            //     sb.appendln("# For nested library sources replace ${LIB_NAME} with library name for each library");
            //     sb.prefix().appendln("set(${LIB_NAME}_RECURSE true)");
            //     sb.line();
            // }
        }

        String programmer = mySettings.getProgrammerId();
        builder.setOrAddCommand("SET_PROGRAMMER", ifNullOrEmpty(programmer, "avrispmkii")).commentOut(programmer.isEmpty());

        String port = mySettings.getPort();
        builder.setOrAddCommand("SET_PORT", ifNullOrEmpty(port, "/dev/cu.usbserial-00000000")).commentOut(port.isEmpty());

        String baudRateText = mySettings.getBaudRateText();
        builder.setOrAddCommand("SET_UPLOAD_SPEED", ifNullOrEmpty(baudRateText, "9600")).commentOut(baudRateText.isEmpty());

        builder.setOrAddCommand("SET_AFLAGS", "-v").commentOut(!mySettings.isVerbose());

        if (isStaticLib) {
            builder.setOrAddCommand("GENERATE_ARDUINO_LIBRARY");
            builder.removeCommand("GENERATE_ARDUINO_FIRMWARE");
        } else {
            builder.setOrAddCommand("GENERATE_ARDUINO_FIRMWARE");
            builder.removeCommand("GENERATE_ARDUINO_LIBRARY");
        }

        // Can add our own values to resolve variables
        return builder.getCMakeContents(null);
    }
}
