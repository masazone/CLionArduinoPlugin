package com.vladsch.clionarduinoplugin.generators.cmake

import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettingsProxy
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectFileSettings
import com.vladsch.clionarduinoplugin.util.helpers.plus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class ArduinoCMakeBuilderTest {
    val testProjects:File = File(ArduinoCMakeListsTxtBuilder::class.java.getResource("/projects/dummy.txt").toURI()).parentFile

    private val source = """
# set(lib1_RECURSE true)
set(lib2_RECURSE true)
set(lib3_RECURSE true)
# set(\$\{LIB_NAME}_RECURSE true)
set(lib4_RECURSE false)
"""

    fun compareCommand(expected: Array<Array<String>>, actual: List<CMakeCommand>) {
        assertEquals("Result size differs", expected.size, actual.size)

        for (i in 0..expected.size - 1) {
            val exp = expected[i]
            val act = actual[i]

            assertEquals("Argument count differs", exp.size, act.argCount)

            for (j in 0..exp.size - 1) {
                assertEquals("Item[$i+1] argument ${j + 1} differs", exp[j], actual[i].getArg(j))
            }
        }
    }

    @Test
    fun test_superType() {
        val builder = ArduinoCMakeListsTxtBuilder(source)
        val commands = builder.getCommands(ArduinoCMakeListsTxtBuilder.SET_LIB_NAMES_RECURSE)

        compareCommand(arrayOf(
                arrayOf("lib2", "true"),
                arrayOf("lib3", "true"),
                arrayOf("lib4", "false")
        ), commands)
    }

    @Test
    fun test_subTypeSuperType() {
        val builder = ArduinoCMakeListsTxtBuilder(source,null, mapOf<String, Any>("LIB_NAME" to "lib3"))
        val commands = builder.getCommands(ArduinoCMakeListsTxtBuilder.SET_LIB_NAMES_RECURSE)

        compareCommand(arrayOf(
                arrayOf("lib2", "true"),
                arrayOf("true"),
                arrayOf("lib4", "false")
        ), commands)
    }

    @Test
    fun test_sketch1() {
        val projectDir = testProjects + "sketch_1"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir)
        val expected = ArduinoApplicationSettingsProxy() as ArduinoProjectFileSettings

        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h")
        expected.sketch = "sketch_1.ino"
        expected.boardId = "pro"
        expected.cpuId="8MHzatmega328"
        expected.programmerId="avrispmkii"
        expected.port="/dev/cu.usbserial-00000000"
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))
    }

    @Test
    fun test_sketch2() {
        val projectDir = testProjects + "sketch_2"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir)
        val expected = ArduinoApplicationSettingsProxy() as ArduinoProjectFileSettings

        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h")
        expected.sketch = "sketch_2.ino"
        expected.boardId = "uno"
        expected.cpuId = ""
        expected.programmerId = "usbtinyisp"
        expected.port = "/dev/cu.Bluetooth-Incoming-Port"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("\${CMAKE_CURRENT_SOURCE_DIR}/libs")
        expected.isVerbose = false

        assertEquals(expected.asString("expected"), settings?.asString("expected"))
    }

    @Test
    fun test_arduino_lib1() {
        val projectDir = testProjects + "arduino_lib1"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir)
        val expected = ArduinoApplicationSettingsProxy() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h", "arduino_lib1.h")
        expected.sources = arrayOf("arduino_lib1_test.cpp", "arduino_lib1.cpp")
        expected.boardId = "pro"
        expected.cpuId = "8MHzatmega328"
        expected.programmerId = "avrispmkii"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("\${CMAKE_CURRENT_SOURCE_DIR}/test")
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))
    }

    @Test
    fun test_arduino_lib2() {
        val projectDir = testProjects + "arduino_lib2"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir)
        val expected = ArduinoApplicationSettingsProxy() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h", "arduino_lib2.h")
        expected.sources = arrayOf("arduino_lib2.cpp", "arduino_lib2_test.cpp")
        expected.boardId = "uno"
        expected.cpuId = ""
        expected.programmerId = "usbtinyisp"
        expected.port = "/dev/cu.ARPT"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("\${CMAKE_CURRENT_SOURCE_DIR}/lib", "\${CMAKE_CURRENT_SOURCE_DIR}/util")
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))
    }

    @Test
    fun test_static_lib1() {
        val projectDir = testProjects + "static_lib1"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir)
        val expected = ArduinoApplicationSettingsProxy() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.libraryType = "static"
        expected.projectName = projectDir.name
        expected.headers = arrayOf("static_lib1.h")
        expected.sources = arrayOf("static_lib1.cpp")
        expected.boardId = "pro"
        expected.cpuId = "16MHzatmega328"
        expected.programmerId = "usbasp"
        expected.port = "/dev/cu.Bluetooth-Incoming-Port"
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))
    }

    @Test
    fun test_static_lib2() {
        val projectDir = testProjects + "static_lib2"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir)
        val expected = ArduinoApplicationSettingsProxy() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.libraryType = "static"
        expected.projectName = projectDir.name
        expected.headers = arrayOf("static_lib2.h")
        expected.sources = arrayOf("static_lib2.cpp")
        expected.boardId = "robotControl"
        expected.cpuId = ""
        expected.programmerId = "buspirate"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("\${CMAKE_CURRENT_SOURCE_DIR}/")
        expected.isVerbose = false

        assertEquals(expected.asString("expected"), settings?.asString("expected"))
    }
}
