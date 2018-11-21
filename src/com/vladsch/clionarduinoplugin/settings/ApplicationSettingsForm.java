/*
 * Copyright (c) 2015-2016 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.clionarduinoplugin.settings;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.resources.ArduinoConfig;
import com.vladsch.clionarduinoplugin.resources.ResourceUtils;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.clionarduinoplugin.util.ui.Settable;
import com.vladsch.clionarduinoplugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ApplicationSettingsForm implements Disposable, ApplicationSettingsListener {
    private static final Logger logger = Logger.getInstance("com.vladsch.clionarduinoplugin.settings");

    JPanel myMainPanel;
    JTextField myAuthorName;
    JTextField myAuthorEMail;
    TextFieldWithBrowseButton myBoardsTxtPath;
    TextFieldWithBrowseButton myProgrammersTxtPath;
    private JLabel myBoardsTxtLabel;
    private JLabel myProgrammersTxtLabel;
    JBCheckBox myBundledBoardsTxt;
    JBCheckBox myBundledProgrammersTxt;
    private JButton myBoardsTxtDiff;
    private JButton myProgrammersTxtDiff;

    //NewProjectSettingsForm myNewSketchForm;
    //NewProjectSettingsForm myNewLibraryForm;

    @NotNull ArduinoConfig myArduinoConfig;

    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<ArduinoApplicationSettings> components;
    final ArduinoApplicationSettings mySettings;

    public ApplicationSettingsForm(ArduinoApplicationSettings settings) {
        mySettings = settings;

        final String boardsTxt = ArduinoConfig.Companion.getBoardsTxtString();
        final String programmersTxt = ArduinoConfig.Companion.getProgrammersTxtString();
        myArduinoConfig = new ArduinoConfig(boardsTxt, programmersTxt);
        components = new SettingsComponents<ArduinoApplicationSettings>() {
            @Override
            protected Settable[] createComponents(ArduinoApplicationSettings i) {
                return new Settable[] {
                        component(myAuthorName, i::getAuthorName, i::setAuthorName),
                        component(myAuthorEMail, i::getAuthorEMail, i::setAuthorEMail),
                        component(myBundledBoardsTxt, i::isBundledBoardsTxt, i::setBundledBoardsTxt),
                        component(myBundledProgrammersTxt, i::isBundledProgrammersTxt, i::setBundledProgrammersTxt),
                        component(myBoardsTxtPath.getTextField(), i::getBoardsTxtPath, i::setBoardsTxtPath),
                        component(myProgrammersTxtPath.getTextField(), i::getProgrammersTxtPath, i::setProgrammersTxtPath),
                        //component(myNewSketchForm, i),
                        //component(myNewLibraryForm, i),
                };
            }
        };

        ApplicationManager.getApplication().getMessageBus().connect(this).subscribe(ApplicationSettingsListener.TOPIC, this);

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateOptions(false);
            }
        };

        DocumentAdapter change = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                updateOptions(false);
            }
        };

        myBundledBoardsTxt.addActionListener(listener);
        myBundledProgrammersTxt.addActionListener(listener);

        //noinspection DialogTitleCapitalization
        myBoardsTxtPath.addBrowseFolderListener(Bundle.message("settings.boards-file-directory.title"), null, null,
                new FileChooserDescriptor(true, false, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        //noinspection DialogTitleCapitalization
        myProgrammersTxtPath.addBrowseFolderListener(Bundle.message("settings.programmers-file-directory.title"), null, null,
                new FileChooserDescriptor(true, false, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        myBoardsTxtPath.getTextField().getDocument().addDocumentListener(change);
        myProgrammersTxtPath.getTextField().getDocument().addDocumentListener(change);

        myBoardsTxtDiff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO: show diff
                File file = new File(myBoardsTxtPath.getText());
                if (!file.exists()) {
                } else if (file.isDirectory()) {
                } else {
                    String fileTxt = ResourceUtils.getFileContent(file);
                    SimpleDiffRequest request = new SimpleDiffRequest(
                            Bundle.message("settings.boards-txt-diff.title"),
                            getContent(boardsTxt),
                            getContent(fileTxt),
                            Bundle.message("settings.bundled.0.title", "boards.txt"),
                            myBoardsTxtPath.getText());
                    showDiff(request);
                }
            }
        });

        myProgrammersTxtDiff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO: show diff
                File file = new File(myProgrammersTxtPath.getText());
                if (!file.exists()) {
                } else if (file.isDirectory()) {
                } else {
                    String fileTxt = ResourceUtils.getFileContent(file);
                    SimpleDiffRequest request = new SimpleDiffRequest(
                            Bundle.message("settings.programmers-txt-diff.title"),
                            getContent(programmersTxt),
                            getContent(fileTxt),
                            Bundle.message("settings.bundled.0.title", "programmers.txt"),
                            myProgrammersTxtPath.getText());
                    showDiff(request);
                }
            }
        });

        updateOptions(true);
    }

    DiffContent getContent(String content) {
        return DiffContentFactoryEx.getInstanceEx().create(content, PlainTextFileType.INSTANCE, true);
    }

    void showDiff(DiffRequest request) {
        if (request == null) return;

        DiffManager.getInstance().showDiff(null, request);
    }

    void updateOptions(boolean onSet) {
        myBoardsTxtPath.setEnabled(!myBundledBoardsTxt.isSelected());
        myProgrammersTxtPath.setEnabled(!myBundledProgrammersTxt.isSelected());

        if (myBundledBoardsTxt.isSelected()) {
            myBoardsTxtDiff.setVisible(false);
        }

        if (myBundledProgrammersTxt.isSelected()) {
            myProgrammersTxtDiff.setVisible(false);
        }

        if (myBoardsTxtPath.isEnabled()) {
            String boardsTxtPath = myBoardsTxtPath.getText();
            String error = "";
            if (boardsTxtPath.isEmpty()) {
                // using bundled boards txt
                error = Bundle.message("settings.bundled-boards-txt.message");
            } else {
                // try to open and read the file
                File file = new File(boardsTxtPath);
                if (!file.exists()) {
                    // should not happen
                    error = "<html><body><span style='color: red'>" + Bundle.message("settings.not-exists-boards-txt.message") + "</span></body></html>";
                } else if (file.isDirectory()) {
                    error = "<html><body><span style='color: red'>" + Bundle.message("settings.directory-boards-txt.message") + "</span></body></html>";
                } else {
                    String fileTxt = ResourceUtils.getFileContent(file);
                    ArduinoConfig arduinoConfig = new ArduinoConfig(fileTxt, "");
                    if (arduinoConfig.getBoardIdMap().size() == 0) {
                        // invalid
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.invalid-boards-txt.message") + "</span></body></html>";
                    }
                }
            }

            if (!error.isEmpty()) {
                myBoardsTxtLabel.setText(error);
                myBoardsTxtLabel.setVisible(true);
                myBoardsTxtDiff.setVisible(false);

                if (onSet) {
                    myBundledBoardsTxt.setSelected(true);
                }
            } else {
                myBoardsTxtLabel.setVisible(false);
                myBoardsTxtDiff.setVisible(true);
            }
        }

        if (myProgrammersTxtPath.isEnabled()) {
            String programmersTxtPath = myProgrammersTxtPath.getText();
            String error = "";
            if (programmersTxtPath.isEmpty()) {
                // using bundled programmers txt
                error = Bundle.message("settings.bundled-programmers-txt.message");
            } else {
                // try to open and read the file
                File file = new File(programmersTxtPath);
                if (!file.exists()) {
                    // should not happen
                    error = "<html><body><span style='color: red'>" + Bundle.message("settings.not-exists-programmers-txt.message") + "</span></body></html>";
                } else if (file.isDirectory()) {
                    error = "<html><body><span style='color: red'>" + Bundle.message("settings.directory-programmers-txt.message") + "</span></body></html>";
                } else {
                    String fileTxt = ResourceUtils.getFileContent(file);
                    ArduinoConfig arduinoConfig = new ArduinoConfig("", fileTxt);
                    if (arduinoConfig.getProgrammerIdMap().size() == 0) {
                        // invalid
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.invalid-programmers-txt.message") + "</span></body></html>";
                    }
                }
            }
            if (!error.isEmpty()) {
                myProgrammersTxtLabel.setText(error);
                myProgrammersTxtLabel.setVisible(true);
                myProgrammersTxtDiff.setVisible(false);

                if (onSet) {
                    myBundledProgrammersTxt.setSelected(true);
                }
            } else {
                myProgrammersTxtLabel.setVisible(false);
                myProgrammersTxtDiff.setVisible(true);
            }
        }
    }

    @Override
    public void onSettingsChanged() {
        reset(mySettings);
    }

    public boolean isModified(@NotNull ArduinoApplicationSettings settings) {
        return components.isModified(settings);
    }

    public void apply(@NotNull ArduinoApplicationSettings settings) {
        if (isModified(settings)) {
            components.apply(settings);
        }
    }

    public void reset(@NotNull ArduinoApplicationSettings settings) {
        if (isModified(settings)) {
            components.reset(settings);
            updateOptions(true);
        }
    }

    @Override
    public void dispose() {

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //myNewSketchForm = new NewProjectSettingsForm(false,true);
        //myNewLibraryForm = new NewProjectSettingsForm(true,true);
    }
}
