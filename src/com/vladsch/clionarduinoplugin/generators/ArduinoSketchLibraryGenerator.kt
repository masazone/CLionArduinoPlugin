/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators

import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.mac.foundation.Foundation
import com.vladsch.clionarduinoplugin.Bundle
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.clionarduinoplugin.util.StudiedWord
import icons.PluginIcons
import org.jetbrains.annotations.Nls

import javax.swing.Icon
import java.io.IOException

class ArduinoSketchLibraryGenerator : ArduinoProjectGeneratorBase(true) {

    @Nls
    override fun getName(): String {
        return Bundle.message("new-project.library-project.name")
    }

    override fun getDescription(): String? {
        return Bundle.message("new-project.library-project.description")
    }

    override fun getLogo(): Icon? {
        return PluginIcons.arduino_library
    }

    @Throws(IOException::class)
    override fun createSourceFiles(name: String, dir: VirtualFile): Array<VirtualFile> {
        var userName: String? = mySettings.authorName
        val email = mySettings.authorEMail

        if (SystemInfo.isMac) {
            val fullUserName = Foundation.fullUserName()
            if (fullUserName != null && !fullUserName.isEmpty()) {
                userName = fullUserName
            }
        }

        val files: Array<VirtualFile>
        val word = StudiedWord(name, StudiedWord.DOT or StudiedWord.DASH or StudiedWord.UNDER)
        val fileName = word.makeScreamingSnakeCase()

        if (ArduinoApplicationSettings.ARDUINO_LIB_TYPE == mySettings.libraryType) {
            files = arrayOf(createProjectFileWithContent(dir, name + Strings.DOT_CPP_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_CPP_CONTENTS
                    .replace("<\$PROJECT_NAME$>", name)
                    .replace("<\$FILE_NAME$>", name)),

                    createProjectFileWithContent(dir, name + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_H_CONTENTS
                            .replace("<\$PROJECT_NAME$>", name)
                            .replace("<\$FILE_NAME$>", fileName)),

                    createProjectFileWithContent(dir, name + "_test" + Strings.DOT_INO_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_TEST_CONTENTS.replace("<\$PROJECT_NAME$>", name)), createProjectFileWithContent(dir, "keywords.txt", Strings.DEFAULT_ARDUINO_LIBRARY_KEYWORDS_CONTENTS.replace("<\$PROJECT_NAME$>", name)),

                    /*
                                        createProjectFileWithContent(dir, "User_Setup" + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_USER_SETUP_H_CONTENTS
                                                .replace("<$PROJECT_NAME$>", name)
                                                .replace("<$FILE_NAME$>", fileName)),
                    */

                    createProjectFileWithContent(dir, "library.properties", Strings.DEFAULT_ARDUINO_LIBRARY_PROPERTIES_CONTENTS
                            .replace("<\$PROJECT_NAME$>", name)
                            .replace("<\$LIBRARY_CATEGORY$>", mySettings.libraryCategory)
                            .replace("<\$USER_NAME$>", userName ?: "Name")
                            .replace("<\$E_MAIL$>", email ?: "<email@example.com>")))
        } else {
            files = arrayOf(createProjectFileWithContent(dir, name + Strings.DOT_CPP_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_CPP_CONTENTS
                    .replace("<\$PROJECT_NAME$>", name)
                    .replace("<\$FILE_NAME$>", name)),

                    createProjectFileWithContent(dir, name + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_H_CONTENTS
                            .replace("<\$PROJECT_NAME$>", name)
                            .replace("<\$FILE_NAME$>", fileName)))
        }
        return files
    }
}
