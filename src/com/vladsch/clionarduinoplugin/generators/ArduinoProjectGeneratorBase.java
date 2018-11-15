/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.GeneratorPeerImpl;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.ui.mac.foundation.Foundation;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.jetbrains.cidr.cpp.CPPLog;
import com.jetbrains.cidr.cpp.cmake.projectWizard.CLionProjectWizardUtils;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeProjectGenerator;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspaceListener;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettingsService;
import com.vladsch.clionarduinoplugin.resources.ArduinoToolchainFiles;
import com.vladsch.clionarduinoplugin.resources.BuildConfig;
import com.vladsch.clionarduinoplugin.resources.BuildConfig.Board;
import com.vladsch.clionarduinoplugin.resources.BuildConfig.Programmer;
import com.vladsch.clionarduinoplugin.resources.Strings;
import icons.PluginIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class ArduinoProjectGeneratorBase extends CMakeProjectGenerator {
    public static final String ARDUINO_PROJECTS_GROUP_NAME = "Arduino";
    public static final String ARDUINO_SKETCH_PROJECT_GENERATOR_NAME = "Arduino Sketch";
    public static final String ARDUINO_SKETCH_LIBRARY_GENERATOR_NAME = "Arduino Library";
    public static final String ARDUINO_LIB_TYPE = "arduino";
    public static final String STATIC_LIB_TYPE = "static";

    private static final ArduinoProjectSettings ARDUINO_MAKE_DEFAULT_PROJECT_SETTINGS = new ArduinoProjectSettings();

    final protected boolean isLibrary;

    @Nullable final protected BuildConfig buildConfig;
    final protected ArduinoProjectSettings mySettings;

    public ArduinoProjectGeneratorBase(final boolean isLibrary) {
        this.isLibrary = isLibrary;

        String buildTxt = BuildConfig.getBuildTxtString();
        String programmersTxt = BuildConfig.getProgrammersTxtString();
        buildConfig = new BuildConfig(buildTxt, programmersTxt);

        mySettings = new ArduinoProjectSettings(ArduinoApplicationSettingsService.getInstance().getState());
    }

    @NotNull
    @Override
    protected String getCMakeFileContent(@NotNull final String projectName) {
        return "";
    }

    @NotNull
    public String getGroupName() {
        return ARDUINO_PROJECTS_GROUP_NAME;
    }

    @Nls
    @NotNull
    abstract public String getName();

    @Nullable
    @Override
    public String getDescription() {
        return "Description";
    }

    @Nullable
    public Icon getLogo() {
        return PluginIcons.arduino_logo;
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NotNull
    public String[] getLibraryTypes() {
        return new String[] { ARDUINO_LIB_TYPE, STATIC_LIB_TYPE };
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NotNull
    public String[] getLibraryCategories() {
        return new String[] {
                "",
                "Communications",
                "Data Processing",
                "Data Storage",
                "Device Control",
                "Display",
                "Other",
                "Sensors",
                "Signal Input/Output",
                "Timing",
        };
    }

    @NotNull
    public String getLanguageVersion() {
        return mySettings.getLanguageVersion();
    }

    @Nullable
    public String getLibraryType() {
        return mySettings.getLibraryType();
    }

    @Nullable
    public String getLibraryDirectory() {
        return mySettings.getLibraryDirectory();
    }

    public boolean addLibrarySettingsPanel() {
        return isLibrary;
    }

    public void setLanguageVersion(@NotNull String languageVersion) {
        mySettings.setLanguageVersion(languageVersion);
    }

    public void setLibraryType(@NotNull String libraryType) {
        mySettings.setLibraryType(libraryType);
    }

    public void setLibraryDirectory(@NotNull String libraryDirectory) {
        mySettings.setLibraryDirectory(libraryDirectory);
    }

    public boolean isAddLibraryDirectory() {
        return mySettings.isAddLibraryDirectory();
    }

    public void setAddLibraryDirectory(final boolean addLibraryDirectory) {
        mySettings.setAddLibraryDirectory(addLibraryDirectory);
    }

    @Nullable
    public String getBoard() {
        return mySettings.getBoard();
    }

    public void setBoard(@NotNull final String board) {
        mySettings.setBoard(board);
        mySettings.setCpu(mySettings.boardCpu.get(board));
    }

    @Nullable
    public String getCpu() {
        return mySettings.getCpu();
    }

    public void setCpu(@NotNull final String cpu) {
        mySettings.setCpu(cpu);
        mySettings.setBoardCpu(mySettings.getBoard(), cpu);
    }

    @Nullable
    public String getProgrammer() {
        return mySettings.programmer;
    }

    public void setProgrammer(@NotNull final String programmer) {
        mySettings.setProgrammer(programmer);
    }

    @Nullable
    public String getPort() {
        return mySettings.getPort();
    }

    public void setPort(@Nullable final String port) {
        mySettings.setPort(port);
        mySettings.addPortHistory(port);
    }

    public boolean isVerbose() {
        return mySettings.isVerbose();
    }

    public void setVerbose(final boolean verbose) {
        mySettings.setVerbose(verbose);
    }

    public boolean isNestedLibrarySources() {
        return mySettings.isNestedLibrarySources();
    }

    public void setRecursiveLibrarySources(final boolean recursiveLibrarySources) {
        mySettings.setNestedLibrarySources(recursiveLibrarySources);
    }

    public String getLibraryCategory() {
        return mySettings.getLibraryCategory();
    }

    public void setLibraryCategory(final String category) {
        mySettings.setLibraryCategory(category);
    }

    public String getAuthorName() {
        return mySettings.getAuthorName();
    }

    public void setAuthorName(final String authorName) {
        mySettings.setAuthorName(authorName);
    }

    public String getAuthorEMail() {
        if (mySettings.getAuthorEMail() == null) {
            String userName = System.getProperty("user.name");

            if (SystemInfo.isMac) {
                String fullUserName = Foundation.fullUserName();
                if (fullUserName != null && !fullUserName.isEmpty()) {
                    userName = fullUserName;
                }
            }

            if (userName == null) {
                userName = "";
            }

            mySettings.setAuthorName(userName);
            return userName;
        }

        return mySettings.getAuthorEMail();
    }

    public void setAuthorEMail(final String authorEMail) {
        mySettings.setAuthorEMail(authorEMail);
    }

    @Nullable
    public String[] getBoardNames() {
        return buildConfig == null ? null : ContainerUtil.map2Array(buildConfig.getBoards().values(), String.class, (board) -> board.name);
    }

    @Nullable
    public String[] getProgrammerNames() {
        return buildConfig == null ? null : ContainerUtil.map2Array(buildConfig.getProgrammers().values(), String.class, (programmer) -> programmer.name);
    }

    @Nullable
    public List<String> getPorts() {
        HashSet<String> ports = new LinkedHashSet<>();
        if (getPort() != null && !getPort().isEmpty()) {
            ports.add(getPort());
        }
        ports.addAll(mySettings.getPortHistory());
        ports.addAll(Arrays.asList(SerialPortList.getPortNames()));
        return new ArrayList<>(ports);
    }

    @SuppressWarnings("MethodMayBeStatic")
    public String[] getLanguageVersions() {
        return ContainerUtil.map2Array(CppLanguageVersions.values(), String.class, CppLanguageVersions::getDisplayString);
    }

    @Nullable
    public Board getBoardFromName(@Nullable String boardName) {
        if (buildConfig != null && boardName != null) {
            return buildConfig.boardFromName(boardName);
        }
        return null;
    }

    @Nullable
    public Programmer getProgrammerFromName(@Nullable String programmerName) {
        if (buildConfig != null && programmerName != null) {
            return buildConfig.programmerFromName(programmerName);
        }
        return null;
    }

    @Nullable
    String[] getBoardCpuNames(@Nullable String boardName) {
        Board board = getBoardFromName(boardName);
        if (board != null && board.cpuList != null) {
            return board.cpuList.values().toArray(new String[0]);
        }
        return null;
    }

    @NotNull
    String getCpuLabel() {
        return buildConfig == null ? BuildConfig.PROCESSOR : buildConfig.getCpuMenu();
    }

    @Nullable
    public String cmakeLanguageVersion() {
        return CppLanguageVersions.fromDisplayString(getLanguageVersion());
    }

    @Nullable
    public String getBoardId() {
        Board board = getBoardFromName(getBoard());
        return board == null ? null : board.id;
    }

    @Nullable
    public String getProgrammerId() {
        Programmer programmer = getProgrammerFromName(getProgrammer());
        return programmer == null ? null : programmer.id;
    }

    @Nullable
    String getCpuId() {
        if (buildConfig != null && getCpu() != null && getBoard() != null) {
            Board board = getBoardFromName(getBoard());
            if (board != null) {
                return board.cpuFromName(getCpu());
            }
        }
        return null;
    }

    protected static String ifNull(String value, String defValue) {
        return value == null ? defValue : value;
    }

    protected static String ifNullOrEmpty(String value, String defValue) {
        return value == null || value.isEmpty() ? defValue : value;
    }

    @NotNull
    protected String getCMakeFileContent(@NotNull String projectName, @NotNull VirtualFile[] sourceFiles) {
        LineStringBuilder sb = new LineStringBuilder("# ");
        boolean isStaticLib = isLibrary && "static".equals(getLibraryType());

        sb.appendln("cmake_minimum_required(VERSION 2.8.4)");
        sb.appendln("set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)");
        String languageVersion = cmakeLanguageVersion();
        if (languageVersion != null) {
            sb.appendln("set(CMAKE_CXX_STANDARD " + languageVersion + ")");
        }
        sb.line();

        sb.appendln("set(PROJECT_NAME " + projectName + ")");
        sb.appendln("set(${CMAKE_PROJECT_NAME}_BOARD " + ifNull(getBoardId(), "mega") + ")");
        String cpu = getCpuId();
        sb.prefixNullOrEmpty(cpu).appendln("set(ARDUINO_CPU " + ifNullOrEmpty(cpu, "8MHzatmega328") + ")");
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

        if (isAddLibraryDirectory() && getLibraryDirectory() != null && !getLibraryDirectory().isEmpty()) {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.");
            sb.appendln("# An Arduino library my_lib will contain files in " + getLibraryDirectory() + "/my_lib/: my_lib.h, my_lib.cpp + any other cpp files");
            sb.appendln("link_directories(${CMAKE_CURRENT_SOURCE_DIR}/" + getLibraryDirectory() + ")");
            sb.line();
        } else {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.");
            sb.appendln("# For this example (libs will contain additional arduino libraries)");
            sb.appendln("# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files");
            sb.prefix().appendln("link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)");
            sb.line();
        }

        if (!isLibrary) {
            if (sketchFile != null) {
                sb.appendln("# For nested library sources replace ${LIB_NAME} with library name for each library");
                sb.prefix().appendln("set(${LIB_NAME}_RECURSE true)");
                sb.line();
            }
        }

        sb.appendln("#### Additional settings for programmer. From programmers.txt");
        String programmer = getProgrammerId();
        sb.prefix(programmer).appendln("set(${CMAKE_PROJECT_NAME}_PROGRAMMER " + ifNull(programmer, "avrispmkii") + ")");
        sb.prefixNullOrEmpty(getPort()).appendln("set(${CMAKE_PROJECT_NAME}_PORT " + ifNullOrEmpty(getPort(), "/dev/cu.usbserial-00000000") + ")");
        sb.prefix().appendln("set(pro.upload.speed 57600)");
        sb.line();
        sb.appendln("## Verbose build process");
        sb.prefix(!isVerbose()).appendln("set(${CMAKE_PROJECT_NAME}_AFLAGS -v)");
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
        ValidationResult result;
        if (StringUtil.isEmptyOrSpaces(baseDirPath)) {
            result = new ValidationResult("Enter project location");
            return result;
        } else {
            File baseDir = new File(baseDirPath);
            if (!baseDir.isAbsolute()) {
                result = new ValidationResult("Project location path should be absolute");
                return result;
            } else if (baseDir.exists() && !baseDir.canWrite()) {
                result = new ValidationResult(String.format("Directory '%s' is not writable.\nPlease choose another directory.", baseDirPath));
                return result;
            } else {
                // validate other fields
                if (isAddLibraryDirectory()) {
                    if (getLibraryDirectory() != null && getLibraryDirectory().startsWith("/")) {
                        result = new ValidationResult(String.format("Library sub-directory '%s' must be relative to project path.", getLibraryDirectory()));
                        return result;
                        //} else {
                        //    File libDir = new File(baseDir.getPath() + "/" + getLibraryDirectory());
                        //    if (libDir.exists() && !libDir.canWrite()) {
                        //        result = new ValidationResult(String.format("Library sub-directory '%s' is not writable.\nPlease choose another sub-directory.", libDir.getPath()));
                        //        return result;
                        //    }
                    }
                }
            }
            result = ValidationResult.OK;
            return result;
        }
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
        busConnection.subscribe(CMakeWorkspaceListener.TOPIC, new CMakeWorkspaceListener() {
            @Override
            public void reloadingFinished(final boolean canceled) {
                busConnection.disconnect();

                if (!canceled) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        // force reload after the first generation cycle is complete
                        workspace.selectProjectDir(workspace.getProjectDir());
                    });
                }
            }
        });
    }

    protected void handleErrorDuringGeneration(@NotNull Project project, Exception e) {
        Messages.showErrorDialog(project, "Cannot create a new project: " + e.getMessage(), "New Project");
        CPPLog.LOG.info(e);
    }

    @NotNull
    public ProjectGeneratorPeer<CMakeProjectSettings> createPeer() {
        JComponent panel = getSettingsPanel();
        if (panel == null) {
            panel = new JPanel();
        }

        ArduinoProjectSettings projectSettings = getArduinoProjectSettings();
        if (projectSettings == null) {
            projectSettings = ARDUINO_MAKE_DEFAULT_PROJECT_SETTINGS;
        }

        GeneratorPeerImpl<CMakeProjectSettings> peer = new GeneratorPeerImpl<>(projectSettings, panel);
        return peer;
    }

    @Nullable
    public ArduinoProjectSettings getArduinoProjectSettings() {
        ArduinoProjectSettings projectSettings = createProjectSettings();
        if (projectSettings != null) {

            // persist settings
            ArduinoApplicationSettingsService.getInstance().loadState(projectSettings);
        }

        return projectSettings;
    }

    @Nullable
    public JComponent getSettingsPanel() {
        ArduinoProjectSettingsPanel panel = createSettingsPanel();
        if (panel != null) {
            panel.init(this);
        }
        return panel;
    }

    protected ArduinoProjectSettingsPanel createSettingsPanel() {
        return new ArduinoProjectSettingsPanel(this);
    }

    @Nullable
    public ArduinoProjectSettings createProjectSettings() {
        return new ArduinoProjectSettings(mySettings);
    }

    CreatedFilesHolder createFiles(String projectName, VirtualFile rootDir) throws IOException {
        String sanitizedName = FileUtil.sanitizeFileName(projectName);
        VirtualFile[] sourceFiles = createSourceFiles(sanitizedName, rootDir);

        VirtualFile cMakeFile = createCMakeFile(sanitizedName, rootDir, sourceFiles);

        VirtualFile[] extraFiles = ArduinoToolchainFiles.copyToDirectory(VfsUtil.findFileByIoFile(VfsUtilCore.virtualToIoFile(rootDir), false));
        if (isAddLibraryDirectory()) {
            File libDir = new File(rootDir.getPath() + "/" + getLibraryDirectory());
            if (!libDir.exists() && !libDir.getCanonicalPath().equals(rootDir.getCanonicalPath())) {
                libDir.mkdirs();
            }
        }
        return new CreatedFilesHolder(cMakeFile, sourceFiles, extraFiles);
    }

    @NotNull
    protected VirtualFile createCMakeFile(@NotNull String name, @NotNull VirtualFile dir, @NotNull VirtualFile[] sourceFiles) throws IOException {
        VirtualFile cMakeLists = createProjectFileWithContent(dir, "CMakeLists.txt", getCMakeFileContent(name, sourceFiles));
        return cMakeLists;
    }

    @SuppressWarnings("MethodMayBeStatic")
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
