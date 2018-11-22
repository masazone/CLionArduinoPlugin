/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.GeneratorPeerImpl;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.messages.MessageBusConnection;
import com.jetbrains.cidr.cpp.CPPLog;
import com.jetbrains.cidr.cpp.cmake.projectWizard.CLionProjectWizardUtils;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeProjectGenerator;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.ui.CMakeSettingsPanel;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspaceListener;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.resources.ArduinoToolchainFiles;
import com.vladsch.clionarduinoplugin.resources.Strings;
import com.vladsch.clionarduinoplugin.settings.NewProjectSettingsForm;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.clionarduinoplugin.util.Utils;
import icons.PluginIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public abstract class ArduinoProjectGeneratorBase extends CMakeProjectGenerator implements Disposable {

    final protected boolean myIsLibrary;
    final protected ArduinoApplicationSettings mySettings;

    public ArduinoProjectGeneratorBase(final boolean isLibrary) {
        this.myIsLibrary = isLibrary;
        mySettings = ArduinoApplicationSettings.getInstance().getState();
    }

    @Override
    public void dispose() {

    }

    @NotNull
    public String getGroupName() {
        return Bundle.message("new-project.group.name");
    }

    @Nls
    @NotNull
    abstract public String getName();

    @Nullable
    public Icon getLogo() {
        return PluginIcons.arduino_logo;
    }

    @NotNull
    @Override
    protected String getCMakeFileContent(@NotNull final String projectName) {
        return "";
    }

    @Override
    @NotNull
    public String getLanguageVersion() {
        return mySettings.getLanguageVersion();
    }

    @Override
    @Nullable
    public String getLibraryType() {
        return mySettings.getLibraryType();
    }

    @Override
    public void setLanguageVersion(@NotNull String languageVersion) {
        mySettings.setLanguageVersion(languageVersion);
    }

    @Override
    public void setLibraryType(@Nullable String libraryType) {
        mySettings.setLibraryType(libraryType);
    }

    @Override
    public String[] getLanguageVersions() {
        return ArduinoApplicationSettings.LANGUAGE_VERSIONS;
    }

    @NotNull
    protected String getCMakeFileContent(@NotNull String projectName, @NotNull VirtualFile[] sourceFiles) {
        LineStringBuilder sb = new LineStringBuilder("# ");
        boolean isStaticLib = myIsLibrary && "static".equals(mySettings.getLibraryType());

        String boardId = Utils.ifNullOrEmpty(mySettings.getBoardId(), "uno");

        sb.appendln("cmake_minimum_required(VERSION 2.8.4)");
        sb.appendln("set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)");
        String languageVersion = CppLanguageVersions.fromDisplayString(getLanguageVersion());
        if (!languageVersion.isEmpty()) {
            sb.appendln("set(CMAKE_CXX_STANDARD " + languageVersion + ")");
        }
        sb.line();

        sb.appendln("set(PROJECT_NAME " + projectName + ")");

        sb.appendln("set(${CMAKE_PROJECT_NAME}_BOARD " + boardId + ")");
        String cpu = mySettings.getCpuId();
        sb.prefixNullOrEmpty(cpu).appendln("set(ARDUINO_CPU " + Utils.ifNullOrEmpty(cpu, "8MHzatmega328") + ")");
        sb.appendln("project(${PROJECT_NAME})");
        sb.line();

        sb.appendln("# Define the source code for cpp files or default arduino sketch files");
        StringBuilder cppFiles = new StringBuilder();
        StringBuilder hFiles = new StringBuilder();
        String sep = "";
        String sketchFile = null;

        for (VirtualFile file : sourceFiles) {
            String ext = file.getExtension();
            if (ext != null) {
                if (ext.equalsIgnoreCase("c") || ext.equalsIgnoreCase(Strings.CPP_EXT)) {
                    cppFiles.append(" ").append(file.getName());
                } else if (ext.equalsIgnoreCase("hpp") || ext.equalsIgnoreCase(Strings.H_EXT)) {
                    hFiles.append(" ").append(file.getName());
                } else if (ext.equalsIgnoreCase(Strings.INO_EXT) || ext.equalsIgnoreCase(Strings.PDE_EXT)) {
                    sketchFile = file.getName();
                }
            }
        }

        if (cppFiles.length() != 0) {
            sb.appendln("set(${PROJECT_NAME}_SRCS " + cppFiles.toString() + ")");
        } else {
            sb.appendln("# set(${PROJECT_NAME}_SRCS " + projectName + Strings.DOT_CPP_EXT + ")");
        }

        if (hFiles.length() != 0) {
            sb.appendln("set(${PROJECT_NAME}_HDRS " + hFiles.toString() + ")");
        }

        sb.appendln("### Additional static libraries to include in the target.");
        sb.appendln("# set(${CMAKE_PROJECT_NAME}_LIBS lib_name)");
        sb.line();

        if (sketchFile != null) {
            sb.appendln("set(${CMAKE_PROJECT_NAME}_SKETCH " + sketchFile + ")");
        } else {
            sb.appendln("# set(${CMAKE_PROJECT_NAME}_SKETCH " + projectName + Strings.DOT_INO_EXT + ")");
        }

        sb.line();

        if (mySettings.isAddLibraryDirectory() && !mySettings.getLibraryDirectory().isEmpty()) {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.");
            sb.appendln("# An Arduino library my_lib will contain files in " + mySettings.getLibraryDirectory() + "/my_lib/: my_lib.h, my_lib.cpp + any other cpp files");
            sb.appendln("link_directories(${CMAKE_CURRENT_SOURCE_DIR}/" + mySettings.getLibraryDirectory() + ")");
            sb.line();
        } else {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.");
            sb.appendln("# For this example (libs will contain additional arduino libraries)");
            sb.appendln("# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files");
            sb.prefix().appendln("link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)");
            sb.line();
        }

        if (!myIsLibrary) {
            //noinspection VariableNotUsedInsideIf
            if (sketchFile != null) {
                sb.appendln("# For nested library sources replace ${LIB_NAME} with library name for each library");
                sb.prefix().appendln("set(${LIB_NAME}_RECURSE true)");
                sb.line();
            }
        }

        sb.appendln("#### Additional settings for programmer. From programmers.txt");
        String programmer = mySettings.getProgrammerId();
        sb.prefix(programmer).appendln("set(${CMAKE_PROJECT_NAME}_PROGRAMMER " + Utils.ifNullOrEmpty(programmer, "avrispmkii") + ")");
        sb.prefixNullOrEmpty(mySettings.getPort()).appendln("set(${CMAKE_PROJECT_NAME}_PORT " + Utils.ifNullOrEmpty(mySettings.getPort(), "/dev/cu.usbserial-00000000") + ")");
        if (mySettings.getBaudRate() > 0) {
            sb.prefix().appendln(String.format("set(%s.upload.speed %s)", boardId, Utils.ifNullOrEmpty(mySettings.getBaudRateText(), "9600")));
        } else {
            sb.prefix().appendln("set(pro.upload.speed 9600)");
        }
        sb.line();
        sb.appendln("## Verbose build process");
        sb.prefix(!mySettings.isVerbose()).appendln("set(${CMAKE_PROJECT_NAME}_AFLAGS -v)");
        sb.line();

        if (isStaticLib) {
            sb.appendln("generate_arduino_library(${CMAKE_PROJECT_NAME})");
        } else {
            sb.appendln("generate_arduino_firmware(${CMAKE_PROJECT_NAME})");
        }
        return sb.toString();
    }

    @NotNull
    abstract protected VirtualFile[] createSourceFiles(@NotNull String name, @NotNull VirtualFile dir) throws IOException;

    @NotNull
    public ValidationResult validate(@NotNull String baseDirPath) {
        if (StringUtil.isEmptyOrSpaces(baseDirPath)) {
            return new ValidationResult("Enter project location");
        }
        File baseDir = new File(baseDirPath);
        if (!baseDir.isAbsolute()) {
            return new ValidationResult("Project location path should be absolute");
        } else if (baseDir.exists() && !baseDir.canWrite()) {
            return new ValidationResult(String.format("Directory '%s' is not writable.\nPlease choose another directory.", baseDirPath));
        } else {
            // validate other fields, but only if the location text field was found so we can trigger another validation
            if (mySettings.getBoardId().isEmpty()) {
                return filterFailure(new ValidationResult(Bundle.message("new-project.no-board")));
            }

            if (mySettings.getBoardCpuNames(mySettings.getBoardName()).length > 0 && mySettings.getCpuId().isEmpty()) {
                return filterFailure(new ValidationResult(Bundle.message("new-project.1.no-cpu", mySettings.getCpuLabel(), mySettings.getBoardName())));
            }

            if (mySettings.isAddLibraryDirectory()) {
                if (mySettings.getLibraryDirectory().startsWith("/")) {
                    return filterFailure(new ValidationResult(String.format("Library sub-directory '%s' must be relative to project path.", mySettings.getLibraryDirectory())));
                }
            }
        }
        return super.validate(baseDirPath);
        //return ValidationResult.OK;
    }

    ValidationResult filterFailure(ValidationResult result) {
        boolean canFail = fireValidationFailed();
        if (canFail) {
            return result;
        }
        // here we have a problem, we'll use defaults and hope for the best
        return ValidationResult.OK;
    }

    @Override
    public void generateProject(@NotNull final Project project, @NotNull final VirtualFile baseDir, @NotNull final CMakeProjectSettings settings, @NotNull final Module module) {
        CreatedFilesHolder createdFilesHolder;
        try {
            createdFilesHolder = ApplicationManager.getApplication().runWriteAction(new ThrowableComputable<CreatedFilesHolder, IOException>() {
                @Override
                public CreatedFilesHolder compute() throws IOException {
                    return createFiles(project.getName(), baseDir);
                }
            });
        } catch (IOException e) {
            handleErrorDuringGeneration(project, e);
            return;
        }

        CLionProjectWizardUtils.reformatProjectFiles(project, createdFilesHolder.cMakeFile, formatSourceFilesAsCpp(), createdFilesHolder.sourceFiles);
        CMakeWorkspace.getInstance(project).selectProjectDir(VfsUtilCore.virtualToIoFile(baseDir));
        if (!ApplicationManager.getApplication().isHeadlessEnvironment()) {
            PsiNavigationSupport.getInstance().createNavigatable(project, createdFilesHolder.cMakeFile, -1).navigate(false);
            Arrays.asList(createdFilesHolder.sourceFiles).forEach((file) -> {
                PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true);
            });
        }

        // vsch: Need to reload the CMakeList.txt to generate build files, first time generation is incorrect
        CMakeWorkspace workspace = CMakeWorkspace.getInstance(project);

        MessageBusConnection busConnection = project.getMessageBus().connect();
        busConnection.subscribe(CMakeWorkspaceListener.TOPIC, new MyCMakeWorkspaceListener(busConnection, workspace));
    }

    private static class MyCMakeWorkspaceListener implements CMakeWorkspaceListener {
        private final MessageBusConnection myBusConnection;
        private final CMakeWorkspace myWorkspace;

        public MyCMakeWorkspaceListener(final MessageBusConnection busConnection, final CMakeWorkspace workspace) {
            myBusConnection = busConnection;
            myWorkspace = workspace;
        }

        @Override
        public void reloadingFinished(final boolean canceled) {
            myBusConnection.disconnect();

            if (!canceled) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    // force reload after the first generation cycle is complete
                    myWorkspace.selectProjectDir(myWorkspace.getProjectDir());
                });
            }
        }
    }

    protected void handleErrorDuringGeneration(@NotNull Project project, Exception e) {
        Messages.showErrorDialog(project, "Cannot create a new project: " + e.getMessage(), "New Project");
        CPPLog.LOG.info(e);
    }

    @Override
    @NotNull
    public ProjectGeneratorPeer<CMakeProjectSettings> createPeer() {
        return new GeneratorPeerImpl<>(mySettings, getSettingsPanel());
    }

    @NotNull
    public JComponent getSettingsPanel() {
        return new ArduinoNewProjectSettingsPanel(mySettings, this);
    }

    interface GeneratorFailedValidationListener {
        boolean onValidationFailed();
    }

    final HashSet<WeakReference<GeneratorFailedValidationListener>> myListeners = new HashSet<>();

    void addValidationListener(GeneratorFailedValidationListener listener) {
        myListeners.add(new WeakReference<>(listener));
    }

    boolean fireValidationFailed() {
        boolean canFail = false;
        ArrayList<WeakReference<GeneratorFailedValidationListener>> invalidListeners = new ArrayList<>(myListeners.size());
        for (WeakReference<GeneratorFailedValidationListener> ref : myListeners) {
            GeneratorFailedValidationListener listener = ref.get();
            if (listener != null) {
                if (listener.onValidationFailed()) {
                    canFail = true;
                } else {
                    invalidListeners.add(ref);
                }
            } else {
                invalidListeners.add(ref);
            }
        }

        myListeners.removeAll(invalidListeners);
        return canFail;
    }

    public static class ArduinoNewProjectSettingsPanel extends CMakeSettingsPanel implements ApplicationSettingsListener, GeneratorFailedValidationListener, Disposable {
        final ArduinoApplicationSettings mySettings;
        private final JPanel myPanel;
        private boolean myHaveFailed = false;

        public ArduinoNewProjectSettingsPanel(@NotNull ArduinoApplicationSettings settings, @NotNull ArduinoProjectGeneratorBase projectGenerator) {
            super(projectGenerator);

            NewProjectSettingsForm form = new NewProjectSettingsForm(settings, projectGenerator.myIsLibrary, true);
            mySettings = settings;

            setLayout(new BorderLayout());

            GridLayoutManager layoutManager = new GridLayoutManager(1, 1);
            myPanel = new JPanel(layoutManager);

            GridConstraints constraints = new GridConstraints();
            constraints.setRow(0);
            constraints.setColumn(0);
            constraints.setAnchor(8);
            constraints.setFill(GridConstraints.FILL_HORIZONTAL);
            myPanel.add(form.getComponent(), constraints);

            add(myPanel, "Center");

            ApplicationManager.getApplication().getMessageBus().connect(this).subscribe(ApplicationSettingsListener.TOPIC, this);
            projectGenerator.addValidationListener(this);
        }

        @Override
        public boolean onValidationFailed() {
            myHaveFailed = getLocationTextField() != null;
            return myHaveFailed;
        }

        @Nullable
        private JTextField getLocationTextField() {
            try {
                JPanel basePanel = (JPanel) myPanel.getParent().getParent().getParent();//.getParent();
                return findLocationTextField(basePanel);
            } catch (Throwable ignored) {
                return null;
            }
        }

        public ArduinoApplicationSettings getSettings() {
            return mySettings;
        }

        @Override
        public void onSettingsChanged() {
            if (myHaveFailed) {
                myHaveFailed = false;
                JTextField textField = getLocationTextField();
                if (textField != null) {
                    textField.setText(textField.getText());
                }
            }
        }

        // vsch: kludge: since this class is not a step in the wizard process it cannot trigger re-validation,
        // or at least I wasn't able to figure out how to do it. Workaround: find the location text field
        // and set it to itself to trigger validation to clear the error
        @Nullable
        protected JTextField findLocationTextField(JComponent jcomp) {
            JTextField field;
            int iMax = jcomp.getComponentCount();

            for (int i = 0; i < iMax; i++) {
                Component comp = jcomp.getComponent(i);
                if (comp instanceof JComponent) {
                    if (jcomp instanceof TextFieldWithBrowseButton) {
                        field = ((TextFieldWithBrowseButton) jcomp).getTextField();
                        return field;
                    }
                    field = findLocationTextField((JComponent) comp);
                    if (field != null) return field;
                }
            }
            return null;
        }

        //// show JComponent hierarchy
        //protected void showChildTree(JComponent jcomp, String lvl) {
        //    int iMax = jcomp.getComponentCount();
        //    for (int i = 0; i < iMax; i++) {
        //        Component comp = jcomp.getComponent(i);
        //        if (comp instanceof JComponent) {
        //            System.out.println(String.format("Component at %s.%d - %s[%d] listeners %d", lvl, i, jcomp.getClass().getSimpleName(), jcomp.getComponentCount(), jcomp.getListeners(ActionListener.class).length));
        //            if (jcomp.getClass().getSimpleName().equals("TextFieldWithBrowseButton")) {
        //                boolean isTF = jcomp instanceof TextFieldWithBrowseButton;
        //                JTextField field = ((TextFieldWithBrowseButton) jcomp).getTextField();
        //                String text = field.getText();
        //                field.setText(text);
        //                int tmp = 0;
        //            }
        //            showChildTree((JComponent) comp, lvl + "." + i);
        //        }
        //    }
        //}

        @Override
        public void dispose() {

        }
    }

    CreatedFilesHolder createFiles(String projectName, VirtualFile rootDir) throws IOException {
        String sanitizedName = FileUtil.sanitizeFileName(projectName);
        VirtualFile[] sourceFiles = createSourceFiles(sanitizedName, rootDir);

        VirtualFile cMakeFile = createCMakeFile(sanitizedName, rootDir, sourceFiles);

        VirtualFile[] extraFiles = ArduinoToolchainFiles.copyToDirectory(VfsUtil.findFileByIoFile(VfsUtilCore.virtualToIoFile(rootDir), false));
        if (mySettings.isAddLibraryDirectory()) {
            File libDir = new File(rootDir.getPath() + "/" + mySettings.getLibraryDirectory());
            if (!libDir.exists() && !libDir.getCanonicalPath().equals(rootDir.getCanonicalPath())) {
                libDir.mkdirs();
            }
        }
        return new CreatedFilesHolder(cMakeFile, sourceFiles, extraFiles);
    }

    @NotNull
    protected VirtualFile createCMakeFile(@NotNull String name, @NotNull VirtualFile dir, @NotNull VirtualFile[] sourceFiles) throws IOException {
        return createProjectFileWithContent(dir, "CMakeLists.txt", getCMakeFileContent(name, sourceFiles));
    }

    protected boolean formatSourceFilesAsCpp() {
        return false;
    }

    @NotNull
    protected VirtualFile createProjectFileWithContent(@NotNull VirtualFile projectDir, String fileName, String fileContent) throws IOException {
        VirtualFile file = projectDir.findOrCreateChildData(this, fileName);
        file.setBinaryContent(fileContent.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static class CreatedFilesHolder {
        final VirtualFile cMakeFile;
        final VirtualFile[] sourceFiles;
        private final VirtualFile[] extraFiles;

        CreatedFilesHolder(VirtualFile file, VirtualFile[] files, VirtualFile[] extras) {
            cMakeFile = file;
            sourceFiles = files;
            extraFiles = extras;
        }
    }
}
