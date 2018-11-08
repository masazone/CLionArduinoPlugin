/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package io.github.francoiscambell.clionarduinoplugin.generators;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.GeneratorPeerImpl;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.cidr.cpp.CPPLog;
import com.jetbrains.cidr.cpp.cmake.projectWizard.CLionProjectWizardUtils;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CLionProjectGenerator;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;
import com.jetbrains.cmake.completion.CMakeRecognizedCPPLanguageStandard;
import icons.PluginIcons;
import io.github.francoiscambell.clionarduinoplugin.components.ArduinoApplicationSettingsService;
import io.github.francoiscambell.clionarduinoplugin.resources.ArduinoToolchainFiles;
import io.github.francoiscambell.clionarduinoplugin.resources.BuildConfig;
import io.github.francoiscambell.clionarduinoplugin.resources.BuildConfig.Board;
import io.github.francoiscambell.clionarduinoplugin.resources.BuildConfig.Programmer;
import io.github.francoiscambell.clionarduinoplugin.resources.Strings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class ArduinoProjectGeneratorBase extends CLionProjectGenerator<ArduinoProjectSettings> {
    public static final String ARDUINO_PROJECTS_GROUP_NAME = "Arduino";
    public static final String ARDUINO_SKETCH_PROJECT_GENERATOR_NAME = "Arduino Sketch";
    public static final String ARDUINO_SKETCH_LIBRARY_GENERATOR_NAME = "Arduino Library";
    public static final String ARDUINO_LIB_TYPE = "arduino";
    public static final String STATIC_LIB_TYPE = "static";

    private static final ArduinoProjectSettings ARDUINO_MAKE_DEFAULT_PROJECT_SETTINGS = new ArduinoProjectSettings();

    final protected boolean isLibrary;

    @Nullable final protected BuildConfig buildConfig;

    public ArduinoProjectGeneratorBase(final boolean isLibrary) {
        myLanguageVersion = CMakeRecognizedCPPLanguageStandard.CPP14.getDisplayString();
        this.isLibrary = isLibrary;

        String buildTxt = BuildConfig.getBuildTxtString();
        String programmersTxt = BuildConfig.getProgrammersTxtString();
        buildConfig = new BuildConfig(buildTxt, programmersTxt);

        ArduinoProjectSettings projectSettings = ArduinoApplicationSettingsService.getInstance().getState();
        myLanguageVersion = projectSettings.languageVersion;
        myLibraryType = projectSettings.libraryType;
        myLibraryDirectory = projectSettings.libraryDirectory;
        myAddLibraryDirectory = projectSettings.addLibraryDirectory;
        myBoard = projectSettings.board;
        myCpu = projectSettings.cpu;
        myProgrammer = projectSettings.programmer;
        myPort = projectSettings.port;
        myVerbose = projectSettings.verbose;
        myBoardCpuMap = new HashMap<>(projectSettings.boardCpu);
    }

    @Nullable
    protected String myLanguageVersion;

    @Nullable
    private String myLibraryType = ARDUINO_LIB_TYPE;

    @Nullable
    String myLibraryDirectory;

    @Nullable Boolean myAddLibraryDirectory;

    @Nullable String myBoard;
    @Nullable String myCpu;
    @Nullable String myProgrammer;
    @Nullable String myPort;
    @Nullable Boolean myVerbose;

    @Nullable HashMap<String, String> myBoardCpuMap;

    @NotNull
    public String getGroupName() {
        return ARDUINO_PROJECTS_GROUP_NAME;
    }

    @Nls
    @NotNull
    abstract public String getName();

    @Nullable
    public Icon getLogo() {
        return PluginIcons.arduino_logo;
    }

    @NotNull
    public String[] getLibraryTypes() {
        return new String[] { ARDUINO_LIB_TYPE, STATIC_LIB_TYPE };
    }

    @Nullable
    public String getLanguageVersion() {
        return myLanguageVersion;
    }

    @Nullable
    public String getLibraryType() {
        return myLibraryType;
    }

    @Nullable
    public String getLibraryDirectory() {
        return myLibraryDirectory;
    }

    public boolean addLibrarySettingsPanel() {
        return isLibrary;
    }

    public void setLanguageVersion(@NotNull String languageVersion) {
        myLanguageVersion = languageVersion;
    }

    public void setLibraryType(@Nullable String libraryType) {
        myLibraryType = libraryType;
    }

    public void setLibraryDirectory(@NotNull String libraryDirectory) {
        myLibraryDirectory = libraryDirectory;
    }

    public boolean isAddLibraryDirectory() {
        return myAddLibraryDirectory != null && myAddLibraryDirectory;
    }

    public void setAddLibraryDirectory(final boolean myAddLibraryDirectory) {
        this.myAddLibraryDirectory = myAddLibraryDirectory;
    }

    @Nullable
    public String getBoard() {
        return myBoard;
    }

    public void setBoard(final String board) {
        myBoard = board;
        myCpu = myBoardCpuMap == null ? null : myBoardCpuMap.get(myBoard);
    }

    @Nullable
    public String getCpu() {
        return myCpu;
    }

    public void setCpu(final String cpu) {
        myCpu = cpu;
        if (myBoardCpuMap == null) {
            myBoardCpuMap = new HashMap<>();
        }
        myBoardCpuMap.put(myBoard, myCpu);
    }

    @Nullable
    public String getProgrammer() {
        return myProgrammer;
    }

    public void setProgrammer(@Nullable final String programmer) {
        myProgrammer = programmer;
    }

    @Nullable
    public String getPort() {
        return myPort;
    }

    public void setPort(@Nullable final String port) {
        myPort = port;
    }

    public boolean isVerbose() {
        return myVerbose != null && myVerbose;
    }

    public void setVerbose(final boolean verbose) {
        myVerbose = verbose;
    }

    @Nullable
    public String[] getBoardNames() {
        return buildConfig == null ? null : ContainerUtil.map2Array(buildConfig.getBoards().values(), String.class, (board) -> {
            return board.name;
        });
    }

    @Nullable
    public String[] getProgrammerNames() {
        return buildConfig == null ? null : ContainerUtil.map2Array(buildConfig.getProgrammers().values(), String.class, (programmer) -> {
            return programmer.name;
        });
    }

    public String[] getLanguageVersions() {
        return ContainerUtil.map2Array(CMakeRecognizedCPPLanguageStandard.values(), String.class, CMakeRecognizedCPPLanguageStandard::getDisplayString);
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
        return myLanguageVersion == null ? null : CMakeRecognizedCPPLanguageStandard.fromDisplayString(myLanguageVersion);
    }

    @Nullable
    public String getBoardId() {
        Board board = getBoardFromName(myBoard);
        return board == null ? null : board.id;
    }

    @Nullable
    public String getProgrammerId() {
        Programmer programmer = getProgrammerFromName(myProgrammer);
        return programmer == null ? null : programmer.id;
    }

    @Nullable
    String getCpuId() {
        if (buildConfig != null && myCpu != null && myBoard != null) {
            Board board = getBoardFromName(myBoard);
            if (board != null) {
                return board.cpuFromName(myCpu);
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
        String sep = "";
        String sketchFile = null;

        for (VirtualFile file : sourceFiles) {
            String ext = file.getExtension();
            if (ext != null) {
                if (ext.equalsIgnoreCase("c") || ext.equalsIgnoreCase(Strings.CPP_EXT)) {
                    cppFiles.append(" ").append(file.getName());
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

        if (sketchFile != null) {
            sb.appendln("set(${CMAKE_PROJECT_NAME}_SKETCH " + sketchFile + ")");
        } else {
            sb.appendln("# set(${CMAKE_PROJECT_NAME}_SKETCH " + projectName + Strings.DOT_INO_EXT + ")");
        }

        sb.line();

        if (myAddLibraryDirectory != null && myAddLibraryDirectory) {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.");
            sb.appendln("# An Arduino library my_lib will contain files in " + myLibraryDirectory + "/my_lib/: my_lib.h, my_lib.cpp + any other cpp files");
            sb.appendln("link_directories(${CMAKE_CURRENT_SOURCE_DIR}/" + myLibraryDirectory + ")");
            sb.line();
        } else {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.");
            sb.appendln("# For this example (libs will contain additional arduino libraries)");
            sb.appendln("# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files");
            sb.prefix().appendln("link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)");
            sb.line();
        }

        sb.appendln("#### Additional settings for for pro mini example, with usb serial programmer. From programmers.txt");
        String programmer = getProgrammerId();
        sb.prefix(programmer).appendln("set(${CMAKE_PROJECT_NAME}_PROGRAMMER " + ifNull(programmer, "avrispmkii") + ")");
        sb.prefixNullOrEmpty(myPort).appendln("set(${CMAKE_PROJECT_NAME}_PORT " + ifNullOrEmpty(myPort, "/dev/cu.usbserial-00000000") + ")");
        sb.prefix().appendln("set(pro.upload.speed 57600)");
        sb.line();
        sb.appendln("## Verbose build process");
        sb.prefix(myVerbose == null || !myVerbose).appendln("set(${CMAKE_PROJECT_NAME}_AFLAGS -v)");
        sb.line();

        sb.appendln("generate_arduino_firmware(${CMAKE_PROJECT_NAME})");
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
                if (myAddLibraryDirectory != null && myAddLibraryDirectory) {
                    if (myLibraryDirectory != null && myLibraryDirectory.startsWith("/")) {
                        result = new ValidationResult(String.format("Library sub-directory '%s' must be relative to project path.", myLibraryDirectory));
                        return result;
                        //} else {
                        //    File libDir = new File(baseDir.getPath() + "/" + myLibraryDirectory);
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
    public void generateProject(@NotNull final Project project, @NotNull final VirtualFile baseDir, @NotNull final ArduinoProjectSettings settings, @NotNull final Module module) {
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
    }

    protected void handleErrorDuringGeneration(@NotNull Project project, Exception e) {
        Messages.showErrorDialog(project, "Cannot create a new project: " + e.getMessage(), "New Project");
        CPPLog.LOG.info(e);
    }

    @NotNull
    public ProjectGeneratorPeer<ArduinoProjectSettings> createPeer() {
        JComponent panel = getSettingsPanel();
        if (panel == null) {
            panel = new JPanel();
        }

        ArduinoProjectSettings projectSettings = getArduinoProjectSettings();
        if (projectSettings == null) {
            projectSettings = ARDUINO_MAKE_DEFAULT_PROJECT_SETTINGS;
        }

        GeneratorPeerImpl<ArduinoProjectSettings> peer = new GeneratorPeerImpl<>(projectSettings, panel);
        return peer;
    }

    @Nullable
    public ArduinoProjectSettings getArduinoProjectSettings() {
        ArduinoProjectSettings projectSettings = createProjectSettings();
        if (projectSettings != null) {
            if (myLanguageVersion != null) {
                projectSettings.setLanguageVersion(myLanguageVersion);
            }

            if (myLibraryType != null) {
                projectSettings.setLibraryType(myLibraryType);
            }

            if (myAddLibraryDirectory != null) {
                projectSettings.setAddLibraryDirectory(myAddLibraryDirectory);
            }

            if (myLibraryDirectory != null) {
                projectSettings.setLibraryDirectory(myLibraryDirectory);
            }

            if (myBoard != null) {
                projectSettings.setBoard(myBoard);
            }

            if (myCpu != null) {
                projectSettings.setCpu(myCpu);
            }

            if (myProgrammer != null) {
                projectSettings.setProgrammer(myProgrammer);
            }

            if (myPort != null) {
                projectSettings.setPort(myPort);
            }

            if (myBoardCpuMap != null) {
                for (Entry<String, String> entry : myBoardCpuMap.entrySet()) {
                    // copy new mappings
                    projectSettings.setBoardCpu(entry.getKey(), entry.getValue());
                }
            }

            if (myVerbose != null) {
                projectSettings.setVerbose(myVerbose);
            }

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
        return new ArduinoProjectSettings(ArduinoApplicationSettingsService.getInstance().getState());
    }

    CreatedFilesHolder createFiles(String projectName, VirtualFile rootDir) throws IOException {
        String sanitizedName = FileUtil.sanitizeFileName(projectName);
        VirtualFile[] sourceFiles = createSourceFiles(sanitizedName, rootDir);

        VirtualFile cMakeFile = createCMakeFile(sanitizedName, rootDir, sourceFiles);

        VirtualFile[] extraFiles = ArduinoToolchainFiles.copyToDirectory(VfsUtil.findFileByIoFile(VfsUtilCore.virtualToIoFile(rootDir), false));
        if (myAddLibraryDirectory != null && myAddLibraryDirectory) {
            File libDir = new File(rootDir.getPath() + "/" + myLibraryDirectory);
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

    protected boolean formatSourceFilesAsCpp() {
        return true;
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
