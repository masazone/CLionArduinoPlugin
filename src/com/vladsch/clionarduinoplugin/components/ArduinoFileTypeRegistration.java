package com.vladsch.clionarduinoplugin.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.vladsch.clionarduinoplugin.resources.Strings;
import org.jetbrains.annotations.NotNull;

public class ArduinoFileTypeRegistration implements BaseComponent {

    public void initComponent() {
        ApplicationManager.getApplication().invokeLater(() ->
                ApplicationManager.getApplication().runWriteAction(() -> {
                    FileType cpp = FileTypeManager.getInstance().getFileTypeByExtension(Strings.CPP_EXT);
                    FileTypeManager.getInstance().associateExtension(cpp, Strings.INO_EXT);
                    FileTypeManager.getInstance().associateExtension(cpp, Strings.PDE_EXT);
                }));
    }

    public void disposeComponent() {
        ApplicationManager.getApplication().invokeLater(() ->
                ApplicationManager.getApplication().runWriteAction(() -> {
                    FileType cpp = FileTypeManager.getInstance().getFileTypeByExtension(Strings.CPP_EXT);
                    FileTypeManager.getInstance().removeAssociatedExtension(cpp, Strings.INO_EXT);
                    FileTypeManager.getInstance().removeAssociatedExtension(cpp, Strings.PDE_EXT);
                }));
    }

    @NotNull
    public String getComponentName() {
        return "ArduinoFileTypeRegistration";
    }
}
