package com.vladsch.clionarduinoplugin;

import com.intellij.openapi.application.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.vladsch.clionarduinoplugin.resources.Strings;

import java.io.*;

public class ArduinoSketchFileCreator {

    public static VirtualFile createSketchFileWithName(final Project project, final VirtualFile directory, final String name) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    VirtualFile sketch = directory.createChildData(this, name);
                    final Document sketchDocument = FileDocumentManager.getInstance().getDocument(sketch);
                    if (sketchDocument != null) {
                        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                            @Override
                            public void run() {
                                sketchDocument.setText(Strings.DEFAULT_ARDUINO_SKETCH_CONTENTS);
                            }
                        }, null, null, sketchDocument);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return directory.findChild(name);
    }
}
