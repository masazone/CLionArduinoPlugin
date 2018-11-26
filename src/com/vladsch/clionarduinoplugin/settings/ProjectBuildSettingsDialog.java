/*
 * Copyright (c) 2015-2018 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Alarm;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.generators.ArduinoProjectGenerator;
import com.vladsch.clionarduinoplugin.generators.cmake.ArduinoCMakeListsTxtBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class ProjectBuildSettingsDialog extends DialogWrapper {
    //private static final Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance("com.vladsch.idea.multimarkdown.settings.license.fetch-dialog");

    private JPanel contentPane;
    private JEditorPane descriptionEditorPane;
    private JPanel myFormPanel;
    NewProjectSettingsForm myNewProjectSettingsForm;

    final Project myProject;
    final VirtualFile myFile;
    final ArduinoApplicationSettingsProxy mySettings;
    final ArduinoApplicationSettingsProxy myResetSettings;
    //final Alarm myAlarm;

    public ProjectBuildSettingsDialog(Project project, ArduinoApplicationSettingsProxy settings, final VirtualFile file) {
        super(project, false);
        myProject = project;
        mySettings = settings;
        myResetSettings = ArduinoApplicationSettingsProxy.copyOf(settings);
        myFile = file;
        //myAlarm = new Alarm();

        init();
        setTitle(Bundle.message("settings.project-build-settings.title"));
        setModal(true);

        myNewProjectSettingsForm.setRunnable(this::updateOptions);
        myNewProjectSettingsForm.reset(myResetSettings.getApplicationSettings());
        updateOptions();
        
        //descriptionEditorPane.setText("" +
        //        "<html>\n" +
        //        "  <head>\n" +
        //        "  <style>\n" +
        //        //"     body.multimarkdown-preview, body.multimarkdown-preview p, body.multimarkdown-preview p.error { background: transparent !important;}\n" +
        //        "     body.multimarkdown-preview, body.multimarkdown-preview div { margin: 0 !important; padding: 0 !important; font-family: sans-serif; font-size: " + JBUI.scale(11) + "px; }\n" +
        //        "     body.multimarkdown-preview p { margin: " + JBUI.scale(10) + "px !important; padding: 0 !important; }\n" +
        //        "     p.error { color: #ec2e5c; }\n" +
        //        "  </style>\n" +
        //        "  </head>\n" +
        //        "  <body class='multimarkdown-preview'>\n" +
        //        "    <p>\n" +
        //        "      " + MultiMarkdownBundle.message("settings.license-fetch.description") + "\n" +
        //        "    </p>\n" +
        //        "  </body>\n" +
        //        "</html>\n" +
        //        "");
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "ArduinoSupport.ProjectBuildSettingsDialog";
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here 
    }

    //void updateOptions() {
    //    if (myAlarm.isDisposed()) return;
    //    
    //    myAlarm.cancelAllRequests();
    //    myAlarm.addRequest(this::updateOptionsRaw, 500);
    //}
    
    void updateOptions() {
        ApplicationManager.getApplication().invokeLater(()->{
            boolean enabled = myNewProjectSettingsForm.isModified(mySettings.getApplicationSettings());
            if (myResetAction != null) {
                myResetAction.setEnabled(enabled);
            }

            if (myOkAction != null) {
                myOkAction.setEnabled(enabled);
            }

            if (myShowDiffAction != null) {
                myShowDiffAction.setEnabled(enabled);
            }
        }, ModalityState.any());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        myNewProjectSettingsForm = new NewProjectSettingsForm(mySettings, false, true);
        myFormPanel.add(myNewProjectSettingsForm.getComponent(), BorderLayout.CENTER);
        return contentPane;
    }

    protected class MyOkAction extends OkAction {
        protected MyOkAction() {
            super();
            putValue(Action.NAME, Bundle.message("settings.project-build-settings.ok.label"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (doValidate(true) == null) {
                getOKAction().setEnabled(true);
            }
            super.doAction(e);
        }
    }

    protected class MyAction extends OkAction {
        final private Runnable runnable;

        protected MyAction(String name, Runnable runnable) {
            super();
            putValue(Action.NAME, name);
            this.runnable = runnable;
        }

        @Override
        protected void doAction(ActionEvent e) {
            runnable.run();
        }
    }

    String getCMakeFileContent() {
        Document document = FileDocumentManager.getInstance().getDocument(myFile);
        if (document != null) {
            return document.getText();
        } else {
            try {
                return new String(myFile.contentsToByteArray());
            } catch (IOException e) {
                return "";
            }
        }
    }

    String getModifiedContent(String content) {
        ArduinoApplicationSettingsProxy saved = ArduinoApplicationSettingsProxy.copyOf(mySettings);
        myNewProjectSettingsForm.apply(mySettings.getApplicationSettings());

        CMakeWorkspace workspace = CMakeWorkspace.getInstance(myProject);
        File projectDir = workspace.getProjectDir();
        
        String modifiedContent = ArduinoCMakeListsTxtBuilder.Companion.getCMakeFileContent(content, projectDir.getName(), mySettings, true);
        mySettings.copyFrom(saved);
        return modifiedContent;
    }

    static DiffContent getContent(String content) {
        return DiffContentFactoryEx.getInstanceEx().create(content, PlainTextFileType.INSTANCE, true);
    }

    MyAction myResetAction = null;
    MyAction myShowDiffAction = null;

    @NotNull
    protected Action[] createLeftSideActions() {
        myResetAction = new MyAction(Bundle.message("settings.project-build-settings.reset.label"), new Runnable() {
            @Override
            public void run() {
                mySettings.copyFrom(myResetSettings);
                myNewProjectSettingsForm.reset(mySettings.getApplicationSettings());
                updateOptions();
            }
        });

        myShowDiffAction = new MyAction(Bundle.message("settings.project-build-settings.show-diff.label"), new Runnable() {
            @Override
            public void run() {
                String original = getCMakeFileContent();
                String modified = getModifiedContent(original);
                SimpleDiffRequest request = new SimpleDiffRequest(
                        Bundle.message("settings.project-build-settings.show-diff.title"),
                        getContent(original),
                        getContent(modified),
                        myFile.getName(),
                        Bundle.message("settings.project-build-settings.modified.title")
                );
                DiffManager.getInstance().showDiff(null, request);
            }
        });

        return new Action[] {
                myResetAction,
                myShowDiffAction,
        };
    }
    
    MyOkAction myOkAction = null;

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        myOkAction = new MyOkAction();
        return new Action[] { myOkAction, getCancelAction() };
    }

    public static boolean showDialog(Project project, ArduinoApplicationSettingsProxy settings, final VirtualFile file) {
        ProjectBuildSettingsDialog dialog = new ProjectBuildSettingsDialog(project, settings, file);
        if (dialog.showAndGet()) {
            final String content = dialog.getCMakeFileContent();
            final String modifiedContent = dialog.getModifiedContent(content);
            if (!modifiedContent.equals(content)) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        Document document = FileDocumentManager.getInstance().getDocument(dialog.myFile);
                        if (document != null) {
                            document.setText(modifiedContent);
                        } else {
                            try {
                                dialog.myFile.setBinaryContent(modifiedContent.getBytes());
                            } catch (IOException e) {
                                Messages.showErrorDialog(project, Bundle.message("settings.project-build-settings.save-failed.message", e.getMessage()), Bundle.message("settings.project-build-settings.save-failed.title"));
                            }
                        }

                        ArduinoProjectGenerator.Companion.reloadCMakeLists(dialog.myProject);
                    }
                });
            }
            return true;
        }
        return false;
    }

    protected ValidationInfo doValidate(boolean loadLicense) {
        ArduinoApplicationSettings settings = mySettings.getApplicationSettings();
        myNewProjectSettingsForm.apply(settings);

        ValidationInfo result = ArduinoProjectGenerator.Companion.validateOptionsInfo(settings, myNewProjectSettingsForm);
        if (result != null) {
            return result;
        }

        return super.doValidate();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return doValidate(false);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myNewProjectSettingsForm.getPreferredFocusedComponent();
    }
}
