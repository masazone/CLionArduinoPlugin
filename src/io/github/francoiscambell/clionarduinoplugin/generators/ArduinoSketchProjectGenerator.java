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

public class ArduinoSketchProjectGenerator extends ArduinoProjectGeneratorBase {
    public ArduinoSketchProjectGenerator() {
        super(false);
    }

    @Nls
    @NotNull
    public String getName() {
        return ARDUINO_SKETCH_PROJECT_GENERATOR_NAME;
    }

    @Nullable
    public Icon getLogo() {
        return PluginIcons.arduino_project;
    }

    @Override
    @NotNull
    protected VirtualFile[] createSourceFiles(@NotNull String name, @NotNull VirtualFile dir) throws IOException {
        VirtualFile[] files = new VirtualFile[] {
                createProjectFileWithContent(dir, dir.getName() + Strings.DOT_INO_EXT, Strings.DEFAULT_ARDUINO_SKETCH_CONTENTS)
        };
        return files;
    }
}
