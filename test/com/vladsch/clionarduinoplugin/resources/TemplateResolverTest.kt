package com.vladsch.clionarduinoplugin.resources

import org.junit.Assert.assertEquals
import org.junit.Test

class TemplateResolverTest {
    val String.prep: String
        get() {
            return replace('@', '$').replace('&', '@')
        }

    fun compareFiles(expected: Map<String, String>, actual: Map<String, String>) {
        val sortedKeys = expected.keys.sorted()
        assertEquals(sortedKeys, actual.keys.sorted())

        for (key in sortedKeys) {
            assertEquals(expected[key], actual[key])
        }
    }

    @Test
    fun test_sketch() {
        val files = TemplateResolver.getTemplates("sketch")
        compareFiles(mapOf(SKETCH_FILE to SKETCH_CONTENT), files)
    }

    @Test
    fun test_library() {
        val files = TemplateResolver.getTemplates("library")
        compareFiles(mapOf(
                LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                LIBRARY_H_FILE to LIBRARY_H_CONTENT
        ), files)
    }

    @Test
    fun test_project_static_library() {
        val files = TemplateResolver.getTemplates("project/library_static")
        compareFiles(mapOf(
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT,
                LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                LIBRARY_H_FILE to LIBRARY_H_CONTENT
        ), files)
    }

    @Test
    fun test_project_arduino_library() {
        val files = TemplateResolver.getTemplates("project/library_arduino")
        compareFiles(mapOf(
                LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                LIBRARY_H_FILE to LIBRARY_H_CONTENT,
                LIBRARY_NAME_TEST_INO_FILE to LIBRARY_NAME_TEST_INO_CONTENT,
                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT,
                KEYWORDS_TXT_FILE to KEYWORDS_TXT_CONTENT,
                LIBRARY_PROPERTIES_FILE to LIBRARY_PROPERTIES_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT
        ), files)
    }

    @Test
    fun test_project_sketch() {
        val files = TemplateResolver.getTemplates("project/sketch")
        compareFiles(mapOf(
                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT,
                PROJECT_NAME_INO_FILE to PROJECT_NAME_INO_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT
        ), files)
    }

    @Test
    fun test_project_sketch_subdir() {
        val files = TemplateResolver.getTemplates("project/sketchsubdir")
        compareFiles(mapOf(
                SUB_LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                SUB_LIBRARY_H_FILE to LIBRARY_H_CONTENT,
                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT,
                PROJECT_NAME_INO_FILE to PROJECT_NAME_INO_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT
        ), files)
    }

    @Test
    fun test_variables() {
        val files = TemplateResolver.getTemplates("project/sketchsubdir")
        val values = mapOf("LIBRARY_NAME" to "libName")
        val templates = TemplateResolver.resolveTemplates(files, values)

        compareFiles(mapOf(
                "sub/libName.cpp" to LIBRARY_CPP_CONTENT.replace("<@LIBRARY_NAME@>","libName"),
                "sub/libName.h" to LIBRARY_H_CONTENT.replace("<@LIBRARY_NAME@>","libName"),
                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT.replace("<@PROJECT_NAME@>",""),
//                PROJECT_NAME_INO_FILE to PROJECT_NAME_INO_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT.replace("<@SET_BOARD@>","")
        ), templates)
    }

    @Test
    fun test_variablesDirective() {
        val files = TemplateResolver.getTemplates("project/sketchsubdir")
        val values = mapOf("PROJECT_NAME" to "projectName")
        val templates = TemplateResolver.resolveTemplates(files, values)

        compareFiles(mapOf(
//                "sub/libName.cpp" to LIBRARY_CPP_CONTENT.replace("<@LIBRARY_NAME@>",""),
//                "sub/libName.h" to LIBRARY_H_CONTENT.replace("<@LIBRARY_NAME@>",""),
                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT.replace("<@PROJECT_NAME@>","projectName"),
                "projectName.ino" to PROJECT_NAME_INO_CONTENT.replace("#include \"<@LIBRARY_NAME@>.h\"  // <@DELETE_IF_BLANK[<@LIBRARY_NAME@>]@>\n",""),
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT.replace("<@SET_BOARD@>","")
        ), templates)
    }

    val SKETCH_FILE = "@SKETCH_NAME@.ino"
    val SKETCH_CONTENT = """#include <Arduino.h>
#include "User_Setup.h"
#include "<@LIBRARY_NAME@>.h"  // <@DELETE_IF_BLANK[<@LIBRARY_NAME@>]@>

void setup() {

}

void loop() {

}
"""
    val LIBRARY_NAME_TEST_INO_FILE = "@LIBRARY_NAME@_test.ino"
    val LIBRARY_NAME_TEST_INO_CONTENT = SKETCH_CONTENT

    val PROJECT_NAME_INO_FILE = "@PROJECT_NAME@.ino"
    val PROJECT_NAME_INO_CONTENT = SKETCH_CONTENT

    val LIBRARY_CPP_FILE = "@LIBRARY_NAME@.cpp"
    val SUB_LIBRARY_CPP_FILE = "sub/@LIBRARY_NAME@.cpp"
    val LIBRARY_CPP_CONTENT = """#include "<@LIBRARY_NAME@>.h"

class <@LIBRARY_NAME@> {

public:

}
"""

    val LIBRARY_H_FILE = "@LIBRARY_NAME@.h"
    val SUB_LIBRARY_H_FILE = "sub/@LIBRARY_NAME@.h"
    val LIBRARY_H_CONTENT = """#ifdef _<LIBRARY_NAME>_H_
#define _<LIBRARY_NAME>_H_

#endif //_<LIBRARY_NAME>_H_
"""

    val USER_SETUP_H_FILE = "User_Setup.h"
    val USER_SETUP_H_CONTENT = """#ifdef _<@PROJECT_NAME@>_USER_SETUP_H_
#define _<@PROJECT_NAME@>_USER_SETUP_H_

#endif //_<@PROJECT_NAME@>_USER_SETUP_H_
"""

    val LIBRARY_PROPERTIES_FILE = "library.properties"
    val LIBRARY_PROPERTIES_CONTENT = """name=<@LIBRARY_NAME@>
version=0.0.0
author=<@USER_NAME@>
maintainer=<@USER_NAME@> <@E_MAIL@>
sentence=Summary
paragraph=Description
category=<@LIBRARY_CATEGORY@>
url=URL
architectures=*
"""

    val KEYWORDS_TXT_FILE = "keywords.txt"
    val KEYWORDS_TXT_CONTENT = """#######################################
# Syntax Coloring Map For <@LIBRARY_NAME@>
#######################################

#######################################
# Datatypes (KEYWORD1)
#######################################

#######################################
# Methods and Functions (KEYWORD2)
#######################################

######################################
# Instances (KEYWORD2)
#######################################

#######################################
# Constants (LITERAL1)
#######################################
"""

    val CMAKELISTS_TXT_FILE = "CMakeLists.txt"
    val CMAKELISTS_TXT_CONTENT = """cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE @{CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD)

set(@{CMAKE_PROJECT_NAME}_BOARD)
set(ARDUINO_CPU)
project(@{CMAKE_PROJECT_NAME})

# Define the source code for cpp files or default arduino sketch files
set(@{CMAKE_PROJECT_NAME}_SRCS)
set(@{CMAKE_PROJECT_NAME}_HDRS)

### Additional static libraries to include in the target.
set(@{CMAKE_PROJECT_NAME}_LIBS)

### Main sketch file
set(@{CMAKE_PROJECT_NAME}_SKETCH)

### Add project directories into the build
add_subdirectory()

### Additional settings to add non-standard or your own Arduino libraries.
# For this example (libs will contain additional arduino libraries)
# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files
# link_directories(@{CMAKE_CURRENT_SOURCE_DIR}/libs)
link_directories()

# For nested library sources replace @{LIB_NAME} with library name for each library
# set(@{LIB_NAME}_RECURSE true)
set(@{LIB_NAME}_RECURSE)

#### Additional settings for programmer. From programmers.txt
set(@{CMAKE_PROJECT_NAME}_PROGRAMMER)
set(@{CMAKE_PROJECT_NAME}_PORT)
set(<&SET_BOARD&>.upload.speed)

## Verbose build process
set(@{CMAKE_PROJECT_NAME}_AFLAGS)

generate_arduino_firmware(@{CMAKE_PROJECT_NAME})
generate_arduino_library(@{CMAKE_PROJECT_NAME})
""".prep
}
