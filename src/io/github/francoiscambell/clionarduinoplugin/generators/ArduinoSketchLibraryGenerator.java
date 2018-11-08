/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package io.github.francoiscambell.clionarduinoplugin.generators;

import com.intellij.openapi.vfs.VirtualFile;
import icons.PluginIcons;
import io.github.francoiscambell.clionarduinoplugin.resources.Strings;
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
        VirtualFile[] files = new VirtualFile[] {
                createProjectFileWithContent(dir, dir.getName() + Strings.DOT_CPP_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_CPP_CONTENTS)
                , createProjectFileWithContent(dir, dir.getName() + Strings.DOT_H_EXT, Strings.DEFAULT_ARDUINO_LIBRARY_H_CONTENTS)
        };
        return files;
    }
}
