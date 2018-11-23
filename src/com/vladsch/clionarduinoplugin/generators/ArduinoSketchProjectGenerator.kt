/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators

import com.intellij.openapi.vfs.VirtualFile
import com.vladsch.clionarduinoplugin.Bundle
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.clionarduinoplugin.util.StudiedWord
import icons.PluginIcons
import org.jetbrains.annotations.Nls

import javax.swing.Icon
import java.io.IOException

class ArduinoSketchProjectGenerator : ArduinoProjectGeneratorBase(false) {

    @Nls
    override fun getName(): String {
        return Bundle.message("new-project.sketch-project.name")
    }

    override fun getDescription(): String? {
        return Bundle.message("new-project.sketch-project.description")
    }

    override fun getLogo(): Icon? {
        return PluginIcons.arduino_project
    }

    @Throws(IOException::class)
    override fun createSourceFiles(name: String, dir: VirtualFile): Array<VirtualFile> {
        val word = StudiedWord(name, StudiedWord.DOT or StudiedWord.DASH or StudiedWord.UNDER)
        val fileName = word.makeScreamingSnakeCase()

        val files = arrayOf(createProjectFileWithContent(dir, name + Strings.DOT_INO_EXT, Strings.DEFAULT_ARDUINO_SKETCH_CONTENTS), createProjectFileWithContent(dir, "User_Setup" + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_USER_SETUP_H_CONTENTS
                .replace("<\$PROJECT_NAME$>", name)
                .replace("<\$FILE_NAME$>", fileName)))
        return files
    }
}
