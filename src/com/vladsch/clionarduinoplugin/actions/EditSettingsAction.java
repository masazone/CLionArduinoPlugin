package com.vladsch.clionarduinoplugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.settings.ProjectSettingsDialog;

public class EditSettingsAction extends DumbAwareAction {
    public EditSettingsAction() {
        super(Bundle.message("action.edit-settings.label"), Bundle.message("actions.edit-settings.description"), AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ProjectSettingsDialog.showDialog(project);
        }
    }
}
