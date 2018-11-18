package com.vladsch.clionarduinoplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.components.ArduinoProjectSettings;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

public class ShowSendOptionsAction extends ToggleAction implements DumbAware {
    public ShowSendOptionsAction() {
        super(Bundle.message("action.show-send-options.label"), Bundle.message("actions.show-send-options.description"), PluginIcons.show_send_options);
    }

    @Override
    public boolean isSelected(@NotNull final AnActionEvent e) {
        Project project = e.getProject();
        boolean selected = false;
        if (project != null) {
            ArduinoProjectSettings projectSettings = ArduinoProjectSettings.getInstance(project);
            selected = projectSettings.isShowSendOptions();
        }
        return selected;
    }

    @Override
    public void setSelected(@NotNull final AnActionEvent e, final boolean state) {
        Project project = e.getProject();
        boolean selected = false;
        if (project != null) {
            ArduinoProjectSettings projectSettings = ArduinoProjectSettings.getInstance(project);
            projectSettings.groupChanges(()->{
                projectSettings.setShowSendOptions(state);
            });
        }
    }
}
