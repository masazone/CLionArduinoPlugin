/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.mac.foundation.Foundation;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.resources.Strings;
import com.vladsch.clionarduinoplugin.util.StudiedWord;
import icons.PluginIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.io.IOException;

public class ArduinoSketchLibraryGenerator extends ArduinoProjectGeneratorBase {
    public ArduinoSketchLibraryGenerator() {
        super(true);
    }

    @Nls
    @NotNull
    public String getName() {
        return ARDUINO_SKETCH_LIBRARY_GENERATOR_NAME;
    }

    @Nullable
    public Icon getLogo() {
        return PluginIcons.arduino_library;
    }

    @Override
    @NotNull
    protected VirtualFile[] createSourceFiles(@NotNull String name, @NotNull VirtualFile dir) throws IOException {
        String userName = mySettings.getAuthorName();
        String email = mySettings.getAuthorEMail();

        if (SystemInfo.isMac) {
            String fullUserName = Foundation.fullUserName();
            if (fullUserName != null && !fullUserName.isEmpty()) {
                userName = fullUserName;
            }
        }

        VirtualFile[] files;
        StudiedWord word = new StudiedWord(name, StudiedWord.DOT | StudiedWord.DASH | StudiedWord.UNDER);
        String fileName = word.makeScreamingSnakeCase();

        if (ArduinoApplicationSettings.ARDUINO_LIB_TYPE.equals(mySettings.getLibraryType())) {
            files = new VirtualFile[] {
                    createProjectFileWithContent(dir, name + Strings.DOT_CPP_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_CPP_CONTENTS
                            .replace("<$PROJECT_NAME$>", name)
                            .replace("<$FILE_NAME$>", name)),

                    createProjectFileWithContent(dir, name + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_H_CONTENTS
                            .replace("<$PROJECT_NAME$>", name)
                            .replace("<$FILE_NAME$>", fileName)),

                    createProjectFileWithContent(dir, name + "_test" + Strings.DOT_INO_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_TEST_CONTENTS.replace("<$PROJECT_NAME$>", name)),
                    createProjectFileWithContent(dir, "keywords.txt", Strings.DEFAULT_ARDUINO_LIBRARY_KEYWORDS_CONTENTS.replace("<$PROJECT_NAME$>", name)),

                    /*
                                        createProjectFileWithContent(dir, "User_Setup" + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_USER_SETUP_H_CONTENTS
                                                .replace("<$PROJECT_NAME$>", name)
                                                .replace("<$FILE_NAME$>", fileName)),
                    */

                    createProjectFileWithContent(dir, "library.properties", Strings.DEFAULT_ARDUINO_LIBRARY_PROPERTIES_CONTENTS
                            .replace("<$PROJECT_NAME$>", name)
                            .replace("<$LIBRARY_CATEGORY$>", mySettings.getLibraryCategory())
                            .replace("<$USER_NAME$>", userName == null ? "Name" : userName)
                            .replace("<$E_MAIL$>", email == null ? "<email@example.com>" : email)),
            };
        } else {
            files = new VirtualFile[] {
                    createProjectFileWithContent(dir, name + Strings.DOT_CPP_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_CPP_CONTENTS
                            .replace("<$PROJECT_NAME$>", name)
                            .replace("<$FILE_NAME$>", name)),

                    createProjectFileWithContent(dir, name + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_H_CONTENTS
                            .replace("<$PROJECT_NAME$>", name)
                            .replace("<$FILE_NAME$>", fileName)),
            };
        }
        return files;
    }
}
