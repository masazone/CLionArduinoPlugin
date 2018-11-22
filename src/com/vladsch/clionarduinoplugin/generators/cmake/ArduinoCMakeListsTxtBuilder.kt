package com.vladsch.clionarduinoplugin.generators.cmake

import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.flexmark.util.options.DataHolder
import com.vladsch.flexmark.util.options.MutableDataSet
import java.io.File

/**
 * Class for creating, reading and modifying CMakeLists.txt
 *
 *
 * A spec CMake parser but only intended for Arduino Support created CMakeLists.txt files:
 *
 *
 * cmake_minimum_required(VERSION 2.8.4)
 * set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
 * set(PROJECT_NAME tft_life)
 *
 *
 * ## This must be set before project call
 * set(${CMAKE_PROJECT_NAME}_BOARD pro)
 * set(ARDUINO_CPU 8MHzatmega328)
 *
 *
 * project(${PROJECT_NAME})
 *
 *
 * # Define the source code
 * set(${PROJECT_NAME}_SRCS tft_life.cpp)
 * #set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
 * link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)
 *
 *
 * #### Uncomment below additional settings as needed.
 * set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
 * set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
 * set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
 * # set(pro.upload.speed 57600)
 *
 *
 * generate_arduino_firmware(${CMAKE_PROJECT_NAME})
 */
class ArduinoCMakeListsTxtBuilder : CMakeListsTxtBuilder {

    constructor() : super(ourCommands, ourAnchors) {}

    constructor(text: CharSequence, options: DataHolder?) : super(ourCommands, ourAnchors, text, options) {}

    constructor(text: CharSequence, options: DataHolder?, values: Map<String, Any>?) : super(ourCommands, ourAnchors, text, options, values) {}

    constructor(cMakeFile: CMakeFile) : super(ourCommands, ourAnchors, cMakeFile) {}

    constructor(cMakeFile: CMakeFile, values: Map<String, Any>?) : super(ourCommands, ourAnchors, cMakeFile, values) {}

    companion object {
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
        val CMAKE_MINIMUM_REQUIRED = CMakeCommandType("CMAKE_MINIMUM_REQUIRED", "cmake_minimum_required", arrayOf("VERSION"), 1, 1)
        val LINK_DIRECTORIES = CMakeCommandType("LINK_DIRECTORIES", "link_directories", arrayOf(), 1, CMakeListsTxtBuilder.INF_MAX_ARGS, true, false, true)
        val ADD_SUBDIRECTORY = CMakeCommandType("ADD_SUBDIRECTORY", "add_subdirectory", arrayOf(), 1, 3, false)
        val PROJECT = CMakeCommandType("PROJECT", "project", arrayOf("\${CMAKE_PROJECT_NAME}"), 1, CMakeListsTxtBuilder.INF_MAX_ARGS, true, false, false)
        val SET = CMakeCommandType("SET", "set", arrayOf(), 1, CMakeListsTxtBuilder.INF_MAX_ARGS)

        val GENERATE_ARDUINO_FIRMWARE = CMakeCommandType("GENERATE_ARDUINO_FIRMWARE", "generate_arduino_firmware", arrayOf("\${CMAKE_PROJECT_NAME}"), 0, 0, true, false, true)
        val GENERATE_ARDUINO_LIBRARY = CMakeCommandType("GENERATE_ARDUINO_LIBRARY", "generate_arduino_library", arrayOf("\${CMAKE_PROJECT_NAME}"), 0, 0, true, false, true)

        // @formatter:off
        val SET_CMAKE_TOOLCHAIN_FILE = CMakeCommandType("SET_CMAKE_TOOLCHAIN_FILE", "set", arrayOf("CMAKE_TOOLCHAIN_FILE"), 1, 1, false, false, true, arrayOf("\${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"))
        val SET_CMAKE_CXX_STANDARD = CMakeCommandType("SET_CMAKE_CXX_STANDARD", "set", arrayOf("CMAKE_CXX_STANDARD"), 1, 1, false, false, true)
        val SET_PROJECT_NAME = CMakeCommandType("SET_PROJECT_NAME", "set", arrayOf("PROJECT_NAME"), 2, 2, false, false, true)
        val SET_BOARD = CMakeCommandType("SET_BOARD", "set", arrayOf("\${CMAKE_PROJECT_NAME}_BOARD"), 1, 1, false, false, true)
        val SET_CPU = CMakeCommandType("SET_CPU", "set", arrayOf("ARDUINO_CPU"), 1, 1, false, false, true)
        val SET_SKETCH = CMakeCommandType("SET_SKETCH", "set", arrayOf("\${CMAKE_PROJECT_NAME}_SKETCH"), 1, 1, false, false, true)
        val SET_PROGRAMMER = CMakeCommandType("SET_PROGRAMMER", "set", arrayOf("\${CMAKE_PROJECT_NAME}_PROGRAMMER"), 1, 1, false, false, true)
        val SET_PORT = CMakeCommandType("SET_PORT", "set", arrayOf("\${CMAKE_PROJECT_NAME}_PORT"), 1, 1, false, false, true)
        val SET_AFLAGS = CMakeCommandType("SET_AFLAGS", "set", arrayOf("\${CMAKE_PROJECT_NAME}_AFLAGS"), 1, CMakeListsTxtBuilder.INF_MAX_ARGS, true, false, true)
        val SET_HDRS = CMakeCommandType("SET_HDRS", "set", arrayOf("\${CMAKE_PROJECT_NAME}_HDRS"), 0, CMakeListsTxtBuilder.INF_MAX_ARGS, true, false, true)
        val SET_SRCS = CMakeCommandType("SET_SRCS", "set", arrayOf("\${CMAKE_PROJECT_NAME}_SRCS"), 0, CMakeListsTxtBuilder.INF_MAX_ARGS, true, false, true)
        // @formatter:on

        // with variables that need to be provided
        val SET_LIB_NAME_RECURSE = CMakeCommandType("SET_LIB_NAME_RECURSE", "set", arrayOf("<\$LIB_NAME$>_RECURSE"), 1, 1, false, false, true)
        val SET_UPLOAD_SPEED = CMakeCommandType("SET_UPLOAD_SPEED", "set", arrayOf("<\$SET_BOARD$>.upload.speed"), 1, 1, false, false, true)

        val ourCommands = arrayOf(
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
        )

        val ourAnchors = arrayOf(
                CMakeCommandAnchor.first(CMAKE_MINIMUM_REQUIRED),
                CMakeCommandAnchor.first(SET_CMAKE_TOOLCHAIN_FILE),
                CMakeCommandAnchor.first(SET_CMAKE_CXX_STANDARD),

                CMakeCommandAnchor.before(PROJECT, SET_PROJECT_NAME),
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
                CMakeCommandAnchor.last(GENERATE_ARDUINO_LIBRARY)
        )

        private val OPTIONS = MutableDataSet()
                .set(CMakeParser.AUTO_CONFIG, true)
                .set(CMakeParser.AST_LINE_END_EOL, true)
                .set(CMakeParser.AST_COMMENTS, true)
                .set(CMakeParser.AST_BLANK_LINES, true)
                .set(CMakeParser.AST_ARGUMENT_SEPARATORS, true)

        private val String.extension: String
            get() {
                val pos = lastIndexOf('.')
                if (pos > 0) substring(pos + 1)
                return "";
            }

        private val String.name: String
            get() {
                return File(this).name
            }

        private fun String.ifEmpty(defValue: String): String {
            return if (isEmpty()) defValue else this;
        }

        fun getCMakeFileContent(template: String, projectName: String, mySettings: ArduinoApplicationSettings, myIsLibrary: Boolean, sourceFiles: Iterable<String>): String {
            val command: CMakeCommand?
            val builder = ArduinoCMakeListsTxtBuilder(template, OPTIONS)
            val isStaticLib = myIsLibrary && "static" == mySettings.libraryType

            builder.isWantCommentedOut = true

            builder.setOrAddCommand(CMAKE_MINIMUM_REQUIRED, "2.8.4")
            builder.setOrAddCommand(SET_CMAKE_TOOLCHAIN_FILE, "\${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
            builder.setOrAddCommand(SET_CMAKE_CXX_STANDARD, mySettings.languageVersionLineForCMake).commentOut(mySettings.languageVersion.isEmpty())

            // not needed, we remove it
            command = builder.getCommand(SET_PROJECT_NAME)
            command?.commentOut(true)

            val boardId = mySettings.boardId.ifEmpty("uno")
            builder.setOrAddCommand(SET_BOARD, boardId)

            val cpuIds = mySettings.arduinoConfig.getBoardById(boardId).cpuIds
            val cpuId = mySettings.cpuId.ifEmpty(cpuIds.firstOrNull() ?: "")
            builder.setOrAddCommand(SET_CPU, cpuId).commentOut(cpuId.isEmpty())

            builder.setOrAddCommand(PROJECT).clearArgs()

            val fileNames = sourceFiles.toList().map { it.name }
            val cppFiles = fileNames.filter { it.extension.matches("(?i:c|cpp|cxx)".toRegex()) }
            val hFiles = fileNames.filter { it.extension.matches("(?i:h|hpp|hxx)".toRegex()) }
            val sketchFile = fileNames.findLast { it.extension.matches("(?i:${Strings.PDE_EXT}|${Strings.INO_EXT})".toRegex()) }

            builder.setOrAddCommand(SET_SRCS, cppFiles).commentOut(cppFiles.isEmpty())
            builder.setOrAddCommand(SET_HDRS, hFiles).commentOut(hFiles.isEmpty())

            // TODO: implement
            // builder.addCommand("SET_LIBS", libFiles).commentOut(libFiles.isEmpty());

            builder.setOrAddCommand(SET_SKETCH, projectName + Strings.DOT_INO_EXT)
                    .commentOut(sketchFile == null)

            builder.setOrAddCommand(LINK_DIRECTORIES, "\${CMAKE_CURRENT_SOURCE_DIR}/" + mySettings.libraryDirectory)
                    .commentOut(!mySettings.isAddLibraryDirectory || mySettings.libraryDirectory.isEmpty())

            if (!myIsLibrary) {
                // TODO: add options for additional libraries and recursion options
                // if (sketchFile != null) {
                //     sb.appendln("# For nested library sources replace ${LIB_NAME} with library name for each library");
                //     sb.prefix().appendln("set(${LIB_NAME}_RECURSE true)");
                //     sb.line();
                // }
            }

            val programmer = mySettings.programmerId
            builder.setOrAddCommand(SET_PROGRAMMER, programmer.ifEmpty("avrispmkii")).commentOut(programmer.isEmpty())

            val port = mySettings.port
            builder.setOrAddCommand(SET_PORT, port.ifEmpty("/dev/cu.usbserial-00000000")).commentOut(port.isEmpty())

            val baudRateText = mySettings.baudRateText
            builder.setOrAddCommand(SET_UPLOAD_SPEED, baudRateText.ifEmpty("9600")).commentOut(baudRateText.isEmpty())

            builder.setOrAddCommand(SET_AFLAGS, "-v").commentOut(!mySettings.isVerbose)

            if (isStaticLib) {
                builder.setOrAddCommand(GENERATE_ARDUINO_LIBRARY)
                builder.removeCommand(GENERATE_ARDUINO_FIRMWARE)
            } else {
                builder.setOrAddCommand(GENERATE_ARDUINO_FIRMWARE)
                builder.removeCommand(GENERATE_ARDUINO_LIBRARY)
            }

            // Can add our own values to resolve variables
            return builder.getCMakeContents(null)
        }
    }
}
