/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators;

import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.resources.Strings;
import com.vladsch.clionarduinoplugin.util.StudiedWord;
import icons.PluginIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.io.IOException;

public class ArduinoSketchProjectGenerator extends ArduinoProjectGeneratorBase {
    public ArduinoSketchProjectGenerator() {
        super(false);
    }

    @Nls
    @NotNull
    public String getName() {
        return Bundle.message("new-project.sketch-project.name");
    }

    @Nullable
    @Override
    public String getDescription() {
        return Bundle.message("new-project.sketch-project.description");
    }

    @Nullable
    public Icon getLogo() {
        return PluginIcons.arduino_project;
    }

    @Override
    @NotNull
    protected VirtualFile[] createSourceFiles(@NotNull String name, @NotNull VirtualFile dir) throws IOException {
        StudiedWord word = new StudiedWord(name, StudiedWord.DOT | StudiedWord.DASH | StudiedWord.UNDER);
        String fileName = word.makeScreamingSnakeCase();

        VirtualFile[] files = new VirtualFile[] {
                createProjectFileWithContent(dir, name + Strings.DOT_INO_EXT, Strings.DEFAULT_ARDUINO_SKETCH_CONTENTS),
                createProjectFileWithContent(dir, "User_Setup" + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_USER_SETUP_H_CONTENTS
                        .replace("<$PROJECT_NAME$>", name)
                        .replace("<$FILE_NAME$>", fileName)),
        };
        return files;
    }
}
