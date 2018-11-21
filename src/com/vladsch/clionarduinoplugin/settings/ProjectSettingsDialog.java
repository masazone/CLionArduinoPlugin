package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.components.ArduinoProjectSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class ProjectSettingsDialog extends DialogWrapper {
    JPanel myMainPanel;
    private ProjectSettingsForm myProjectSettingsForm;

    public ProjectSettingsDialog(@NotNull Project project) {
        super(false);

        myMainPanel = new JPanel();

        setTitle(Bundle.message("settings.project-settings.title"));

        ArduinoProjectSettings projectSettings = ArduinoProjectSettings.getInstance(project);
        myProjectSettingsForm = new ProjectSettingsForm(projectSettings, true);
        myMainPanel.add(myProjectSettingsForm.getComponent(), BorderLayout.CENTER);
        myProjectSettingsForm.reset(projectSettings);

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myMainPanel;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        return new Action[] { getOKAction(), getCancelAction() };
    }

    public static void showDialog(@NotNull Project project) {
        ProjectSettingsDialog dialog = new ProjectSettingsDialog(project);
        boolean save = dialog.showAndGet();
        if (save) {
            dialog.myProjectSettingsForm.apply(ArduinoProjectSettings.getInstance(project));
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        //String error = checkRegEx(myPattern);
        //
        //if (!error.isEmpty()) {
        //    return new ValidationInfo(error, myPattern);
        //}
        return super.doValidate();
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "com.vladsch.clionarduinoplugin.settings.ProjectSettingsDialog";
    }

    //@Nullable
    //@Override
    //public JComponent getPreferredFocusedComponent() {
    //    return myProjectSettingsForm.mySearchEditor != null ? myProjectSettingsForm.mySearchEditor.getContentComponent() : myMainPanel;
    //}
}
