package com.vladsch.clionarduinoplugin.actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.vladsch.clionarduinoplugin.ArduinoSketchFileCreator;
import com.vladsch.clionarduinoplugin.resources.Strings;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class NewSketchFile extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final IdeView view = e.getData(LangDataKeys.IDE_VIEW);

        if (project != null && view != null) {
            PsiDirectory directory = view.getOrChooseDirectory();
            if (directory == null) {
                return;
            }
            final VirtualFile directoryVirtualFile = directory.getVirtualFile();

            boolean loop = false;
            do {
                String filename = getDesiredFilename(project);
                if (filename == null) { //cancel
                    return;
                }
                if (filename.isEmpty()) { //no name entered
                    showEmptyFilenameError(project);
                    loop = true;
                    continue;
                }
                filename = correctExtension(filename); //add .ino if the current filename doesn't end with .ino or .pde

                VirtualFile existingFile = directoryVirtualFile.findChild(filename);
                if (existingFile != null) {
                    int overwriteChoice = getOverwriteChoice(project); //ask to overwrite file
                    switch (overwriteChoice) {
                        case Messages.YES:
                            deleteVirtualFile(existingFile);
                            loop = false;
                            break;
                        case Messages.NO:
                            loop = true;
                            continue;
                        case Messages.CANCEL:
                            return;
                    }
                }
                VirtualFile sketch = ArduinoSketchFileCreator
                        .createSketchFileWithName(project, directoryVirtualFile, filename);
                //            ArduinoSketchFileCreator.addFileToCMakeLists(project, sketch); //not sure if i need to do this or not
                FileEditorManager.getInstance(project).openFile(sketch, true, true); //open in editor
            } while (loop);
        }

    }

    private void showEmptyFilenameError(Project project) {
        Messages.showErrorDialog(project, Strings.ENTER_FILENAME, Strings.ERROR);
    }

    private int getOverwriteChoice(Project project) {
        return Messages
                .showYesNoCancelDialog(project, Strings.QUESTION_OVERWRITE, Strings.FILE_ALREADY_EXISTS, PluginIcons.arduino_logo);
    }

    private String getDesiredFilename(Project project) {
        return Messages
                .showInputDialog(project, Strings.ENTER_FILENAME, Strings.SKETCH_NAME, PluginIcons.arduino_logo);
    }

    @NotNull
    private String correctExtension(String filename) {
        if (!(filename.endsWith(Strings.DOT_INO_EXT) || filename.endsWith(Strings.DOT_PDE_EXT))) {
            filename = filename + Strings.DOT_INO_EXT;
        }
        return filename;
    }

    private void deleteVirtualFile(final VirtualFile virtualFile) {
        if (virtualFile == null) {
            return;
        }
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    virtualFile.delete(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
