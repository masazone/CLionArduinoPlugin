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
import com.intellij.ide.diff.DiffElement;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diff.DirDiffManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.resources.ArduinoConfig;
import com.vladsch.clionarduinoplugin.resources.ResourceUtils;
import com.vladsch.clionarduinoplugin.resources.TemplateResolver;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.clionarduinoplugin.util.ui.Settable;
import com.vladsch.clionarduinoplugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class ApplicationSettingsForm implements Disposable, ApplicationSettingsListener {
    static final Logger LOG = Logger.getInstance("com.vladsch.clionarduinoplugin.settings");

    JPanel myMainPanel;
    JTextField myAuthorName;
    JTextField myAuthorEMail;

    JBCheckBox myBundledBoardsTxt;
    TextFieldWithBrowseButton myBoardsTxtPath;
    private JLabel myBoardsTxtLabel;
    private JButton myBoardsTxtDiff;

    JBCheckBox myBundledProgrammersTxt;
    TextFieldWithBrowseButton myProgrammersTxtPath;
    private JLabel myProgrammersTxtLabel;
    private JButton myProgrammersTxtDiff;

    JBCheckBox myBundledTemplates;
    TextFieldWithBrowseButton myTemplatesPath;
    private JLabel myTemplatesLabel;
    private JButton myTemplatesDiff;
    private JButton myTemplatesCreate;

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
                        component(myBundledTemplates, i::isBundledTemplates, i::setBundledTemplates),
                        component(myBoardsTxtPath.getTextField(), i::getBoardsTxtPath, i::setBoardsTxtPath),
                        component(myProgrammersTxtPath.getTextField(), i::getProgrammersTxtPath, i::setProgrammersTxtPath),
                        component(myTemplatesPath.getTextField(), i::getTemplatesPath, i::setTemplatesPath),
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
        myBundledTemplates.addActionListener(listener);

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

        //noinspection DialogTitleCapitalization
        myTemplatesPath.addBrowseFolderListener(Bundle.message("settings.templates-file-directory.title"), null, null,
                new FileChooserDescriptor(false, true, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        myBoardsTxtPath.getTextField().getDocument().addDocumentListener(change);
        myProgrammersTxtPath.getTextField().getDocument().addDocumentListener(change);
        myTemplatesPath.getTextField().getDocument().addDocumentListener(change);

        myBoardsTxtDiff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // show diff
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
                // TODO: copy files
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

        //myTemplatesDiff.addActionListener(new ActionListener() {
        //    @Override
        //    public void actionPerformed(final ActionEvent e) {
        //        // TODO: show diff
        //        File file = new File(myTemplatesPath.getText());
        //        if (!file.exists()) {
        //        } else if (!file.isDirectory()) {
        //        } else {
        //            final Project project = ProjectUtil.guessCurrentProject(myMainPanel);
        //            DirDiffManager diffManager = DirDiffManager.getInstance(project);
        //            DiffElement<VirtualFile> templateElem = getDirContent(diffManager, file);
        //            File bundledTemplateDir = TemplateResolver.INSTANCE.getTemplatesDirectory();
        //            DiffElement<VirtualFile> bundledTemplateElem = getDirContent(diffManager, bundledTemplateDir);
        //
        //            if (templateElem != null && bundledTemplateElem != null && diffManager.canShow(bundledTemplateElem, templateElem)) {
        //                diffManager.showDiff(bundledTemplateElem, templateElem);
        //            }
        //        }
        //    }
        //});

        myTemplatesCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // copy templates to location
                String templatesPathText = myTemplatesPath.getText();
                if (!myBundledTemplates.isSelected() && !templatesPathText.trim().isEmpty()) {
                    File file = new File(templatesPathText);
                    if (!file.exists()) {
                        File parent = file.getParentFile();
                        boolean canCreate = parent != null && parent.canRead() && parent.canWrite();
                        if (canCreate) {
                            try {
                                TemplateResolver.INSTANCE.copyTemplatesDirectoryTo(file);
                                //FileUtil.copyDir(bundledTemplateDir, file);
                                updateOptions(false);
                                Messages.showMessageDialog(
                                        myMainPanel,
                                        Bundle.message("settings.templates-create-success.label", file.getPath()),
                                        Bundle.message("settings.templates-create-success.title"),
                                        Messages.getErrorIcon());
                            } catch (IOException e1) {
                                LOG.info("Create template dir failed", e1);

                                Messages.showMessageDialog(
                                        myMainPanel,
                                        Bundle.message("settings.templates-create-failed.label"),
                                        Bundle.message("settings.templates-create-failed.title"),
                                        Messages.getErrorIcon());

                            }
                        }
                    }
                }
            }
        });

        updateOptions(true);
    }

    DiffContent getContent(String content) {
        return DiffContentFactoryEx.getInstanceEx().create(content, PlainTextFileType.INSTANCE, true);
    }

    @Nullable DiffElement getDirContent(@NotNull DirDiffManager diffManager, File content) {
        try {
            VirtualFile dir = VirtualFileManager.getInstance().findFileByUrl(content.toURI().toURL().toString());
            if (dir != null) {
                return diffManager.createDiffElement(dir);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    void showDiff(DiffRequest request) {
        if (request == null) return;

        DiffManager.getInstance().showDiff(null, request);
    }

    void updateOptions(boolean onSet) {
        myBoardsTxtPath.setEnabled(!myBundledBoardsTxt.isSelected());
        myProgrammersTxtPath.setEnabled(!myBundledProgrammersTxt.isSelected());
        myTemplatesPath.setEnabled(!myBundledTemplates.isSelected());

        if (myBundledBoardsTxt.isSelected()) {
            myBoardsTxtDiff.setVisible(false);
            myBoardsTxtLabel.setVisible(true);
        }

        if (myBundledProgrammersTxt.isSelected()) {
            myProgrammersTxtDiff.setVisible(false);
            myProgrammersTxtLabel.setVisible(true);
        }

        if (myBundledTemplates.isSelected()) {
            myTemplatesDiff.setVisible(false);
            myTemplatesCreate.setVisible(false);
            myTemplatesLabel.setVisible(true);
        }

        if (myBoardsTxtPath.isEnabled()) {
            String boardsTxtPath = myBoardsTxtPath.getText();
            String error = "";
            boolean canShowDiff = false;
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
                    canShowDiff = true;
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
                myBoardsTxtDiff.setVisible(canShowDiff);

                if (onSet) {
                    myBundledBoardsTxt.setSelected(true);
                }
            } else {
                myBoardsTxtLabel.setVisible(false);
                myBoardsTxtDiff.setVisible(true);
            }
        } else {
            String error = Bundle.message("settings.bundled-boards-txt.message");
            myBoardsTxtLabel.setText(error);
        }

        if (myProgrammersTxtPath.isEnabled()) {
            String programmersTxtPath = myProgrammersTxtPath.getText();
            String error = "";
            boolean canShowDiff = false;
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
                    canShowDiff = true;
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
                myProgrammersTxtDiff.setVisible(canShowDiff);

                if (onSet) {
                    myBundledProgrammersTxt.setSelected(true);
                }
            } else {
                myProgrammersTxtLabel.setVisible(false);
                myProgrammersTxtDiff.setVisible(true);
            }
        } else {
            String error = Bundle.message("settings.bundled-programmers-txt.message");
            myProgrammersTxtLabel.setText(error);
        }

        if (myTemplatesPath.isEnabled()) {
            String templatesPath = myTemplatesPath.getText();
            String error = "";
            boolean canCreate = false;
            boolean canShowDiff = false;
            if (templatesPath.isEmpty()) {
                // using bundled programmers txt
                error = Bundle.message("settings.bundled-templates.message");
            } else {
                // try to open and read the file
                File file = new File(templatesPath);
                if (!file.exists()) {
                    // should not happen
                    File parent = file.getParentFile();
                    canCreate = parent != null && parent.canRead() && parent.canWrite();
                    if (!canCreate) {
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.not-exists-templates-rw.message") + "</span></body></html>";
                    } else {
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.not-exists-templates.message") + "</span></body></html>";
                    }
                } else if (file.isFile()) {
                    error = "<html><body><span style='color: red'>" + Bundle.message("settings.file-templates.message") + "</span></body></html>";
                } else {
                    canShowDiff = false; // does not work for non-project files

                    // should have all files in respective template types
                    if (!TemplateResolver.INSTANCE.haveAllTemplates(file)) {
                        // invalid
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.invalid-templates.message") + "</span></body></html>";
                    }
                }
            }

            if (!error.isEmpty()) {
                myTemplatesLabel.setText(error);
                myTemplatesLabel.setVisible(true);
                myTemplatesDiff.setVisible(canShowDiff);
                myTemplatesCreate.setVisible(canCreate);

                if (onSet) {
                    myBundledTemplates.setSelected(true);
                }
            } else {
                myTemplatesLabel.setVisible(false);
                myTemplatesDiff.setVisible(canShowDiff);
                myTemplatesCreate.setVisible(false);
            }
        } else {
            String error = Bundle.message("settings.bundled-templates.message");
            myTemplatesLabel.setText(error);
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
