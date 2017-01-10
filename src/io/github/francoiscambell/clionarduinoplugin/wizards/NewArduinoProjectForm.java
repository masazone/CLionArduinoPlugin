package io.github.francoiscambell.clionarduinoplugin.wizards;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.jetbrains.cidr.cpp.cmake.projectWizard.CMakeProjectStepAdapter;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.LINE_END;
import static java.awt.GridBagConstraints.LINE_START;

/**
 * Created by radford on 1/9/17.
 */
public class NewArduinoProjectForm extends CMakeProjectStepAdapter {
    private JTextField projectNameTextField;
    private TextFieldWithBrowseButton projectPathField;

    private JPanel mainPanel;
    private String lastSelectedPath;

    public NewArduinoProjectForm(String defaultProjectName, String defaultProjectPath) {
        lastSelectedPath = defaultProjectPath;

        mainPanel = new JPanel(new GridBagLayout());

        projectNameTextField = new JTextField(defaultProjectName, 20);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.ipadx = 5;
        constraints.fill = HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.anchor = LINE_END;

        mainPanel.add(new JLabel("Project Name"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.8;
        constraints.anchor = LINE_START;

        mainPanel.add(projectNameTextField, constraints);

        projectPathField = new TextFieldWithBrowseButton(
                new JTextField(
                        defaultProjectPath,
                        20),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lastSelectedPath = projectPathField.getText();
                    }
                }
        );

        projectPathField.addBrowseFolderListener(
                "Select Target Project Directory",
                null,
                null,
                new FileChooserDescriptor(false,
                        true,
                        false,
                        false,
                        false,
                        false));


        projectNameTextField.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        syncFields();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        syncFields();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        syncFields();
                    }

                    private void syncFields() {
                        String projectName = "/" + projectNameTextField.getText();
                        if (!lastSelectedPath.endsWith(projectName)) {
                            projectPathField.setText(lastSelectedPath.concat(projectName));
                            projectPathField.setAutoscrolls(true);
                        }
                    }
                }
        );

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.2;
        constraints.anchor = LINE_END;
        mainPanel.add(new JLabel("Project Path"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0.8;
        constraints.anchor = LINE_START;
        mainPanel.add(projectPathField, constraints);
    }

    @Override
    protected void init() {
        mainPanel.setVisible(true);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mainPanel;
    }

    @Override
    public void dispose() {
        mainPanel.setVisible(false);
    }

    public String getName() {
        return projectNameTextField.getText().replaceAll("-", "_");
    }

    public String getLocation() {
        return projectPathField.getText();
    }

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }
}
