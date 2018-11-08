package io.github.francoiscambell.clionarduinoplugin.wizards;

import com.intellij.ide.RecentProjectsManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.jetbrains.cidr.cpp.CPPLog;
import com.jetbrains.cidr.cpp.cmake.CMakeProjectOpenProcessor;
import com.jetbrains.cidr.cpp.cmake.projectWizard.CMakeProjectWizard;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;
import io.github.francoiscambell.clionarduinoplugin.CMakeListsEditor;
import io.github.francoiscambell.clionarduinoplugin.resources.ArduinoToolchainFiles;
import io.github.francoiscambell.clionarduinoplugin.resources.Strings;
import org.jdom.JDOMException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static com.jetbrains.cidr.cpp.cmake.projectWizard.CLionProjectWizardUtils.refreshProjectDir;

/**
 * Created by francois on 15-08-14.
 */
public class NewArduinoProjectWizard extends CMakeProjectWizard {

    private String lastDir = Optional.ofNullable(RecentProjectsManager.getInstance().getLastProjectCreationLocation())
            .orElse("");

    private NewArduinoProjectForm adapter = new NewArduinoProjectForm(
            "untitled-0",
            new File(lastDir).getPath());

    public NewArduinoProjectWizard() {
        super("New Arduino Sketch Project", "NewArduinoSketchProject");
        initWithStep(adapter);
    }

    @Override
    protected boolean tryFinish() {
        String projectRootPath = adapter.getLocation();
        boolean preExistingProject = false;

        File projectRootDir = new File(projectRootPath);
        if (projectRootDir.exists()) {
            String[] fileList = projectRootDir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return !".DS_Store".equalsIgnoreCase(name) && !"Thumbs.db".equalsIgnoreCase(name);
                }
            });

            if (fileList != null && fileList.length > 0) {
                int dialogAnswer = Messages.showYesNoDialog(String
                        .format("Directory \'%s\' already exists and not empty.\nWould you like to continue?", projectRootPath), "Project Directory Already Exists", Messages
                        .getQuestionIcon());
                if (dialogAnswer != 0) {
                    return false;
                }
                preExistingProject = true;
            }
        } else {
            try {
                VfsUtil.createDirectories(projectRootPath);
            } catch (IOException | RuntimeException e) {
                CPPLog.LOG.warn(e);
                return false;
            }
        }

        String projectRootDirParentPath = projectRootDir.getParent();
        if (projectRootDirParentPath != null) {
            RecentProjectsManager.getInstance().setLastProjectCreationLocation(projectRootDirParentPath);
        }

        try {
            // vsch: need to create the project to have .idea initialized and avoid a bunch of exceptions later
            if (!preExistingProject) {
                Project project = ProjectManager.getInstance().createProject(this.adapter.getName(), projectRootPath);
                int tmp = 0;
            }

            createProject(this.adapter.getName(), projectRootPath);
            return true;
        } catch (IOException e) {
            CPPLog.LOG.warn(e);
            return false;
        }
    }

    public static String createProject(String projectName, String projectRootPath) throws IOException {
        File projectRoot = new File(projectRootPath);
        File cMakeLists = new File(projectRoot, "CMakeLists.txt");
        if (!cMakeLists.exists() && !cMakeLists.createNewFile()) {
            throw new IOException("Cannot create file " + cMakeLists);
        } else {
            projectName = FileUtil.sanitizeFileName(projectName);
            File mainSketchFile = new File(projectRoot, projectName + ".ino");
            if (!mainSketchFile.exists() && !mainSketchFile.createNewFile()) {
                throw new IOException("Cannot create file " + mainSketchFile);
            } else {
                FileUtil.writeToFile(mainSketchFile, Strings.DEFAULT_ARDUINO_SKETCH_CONTENTS);

                VirtualFile cMakeListsVirtualFile = VfsUtil.findFileByIoFile(cMakeLists, true);
                CMakeListsEditor cMakeListsEditor = CMakeListsEditor.getInstance(cMakeListsVirtualFile);
                cMakeListsEditor.clear();
                cMakeListsEditor.minVersion("2.8.4");
                cMakeListsEditor.set("CMAKE_TOOLCHAIN_FILE", "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake");
                cMakeListsEditor.set("PROJECT_NAME", projectName);
                cMakeListsEditor.blankLine();
                cMakeListsEditor.appendLine("set(${CMAKE_PROJECT_NAME}_BOARD mega)");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.appendLine("### If for example using Pro Mini then set board to pro and need to set ARDUINO_CPU");
                cMakeListsEditor.appendLine("## To find the CPU string, RegEx Search for /pro.menu.cpu.([\\w])+.build.mcu=/ in boards.txt");
                cMakeListsEditor.appendLine("## ([\\w+]) above is a placeholder for the string to use for the ARDUINO_CPU");
                cMakeListsEditor.appendLine("# set(${CMAKE_PROJECT_NAME}_BOARD pro)");
                cMakeListsEditor.appendLine("# ARDUINO_CPU must be set before project()");
                cMakeListsEditor.appendLine("# set(ARDUINO_CPU 8MHzatmega328)");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.project("${PROJECT_NAME}");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.appendLine("# Define the source code for cpp files or default arduino sketch files");
                cMakeListsEditor.appendLine("# set(${PROJECT_NAME}_SRCS " + projectName + ".cpp)");
                cMakeListsEditor.set("${CMAKE_PROJECT_NAME}_SKETCH", projectName + ".ino");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.appendLine("#### Additional settings to add non-standard or your own Arduino libraries.");
                cMakeListsEditor.appendLine("# For this example (libs will contain additional arduino libraries)");
                cMakeListsEditor.appendLine("# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files");
                cMakeListsEditor.appendLine("# link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.appendLine("#### Additional settings for for pro mini example, with usb serial programmer.");
                cMakeListsEditor.appendLine("## Don't forget to uncomment the set ARDUINO_CPU above as per boards.txt.");
                cMakeListsEditor.appendLine("# set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)");
                cMakeListsEditor.appendLine("# set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)");
                cMakeListsEditor.appendLine("# set(${CMAKE_PROJECT_NAME}_AFLAGS -v)");
                cMakeListsEditor.appendLine("# set(pro.upload.speed 57600)");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.appendLine("#### Uncomment below additional settings as needed.");
                cMakeListsEditor.appendLine("# set(${CMAKE_PROJECT_NAME}_BOARD mega)");
                cMakeListsEditor.appendLine("# set(${CMAKE_PROJECT_NAME}_PORT /dev/ttyACM0)");
                cMakeListsEditor.appendLine("# set(mega.build.mcu atmega2560)");
                cMakeListsEditor.appendLine("# set(mega.upload.protocol wiring)");
                cMakeListsEditor.appendLine("# set(mega.upload.speed 115200)");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.method("generate_arduino_firmware", "${CMAKE_PROJECT_NAME}");

                ArduinoToolchainFiles.copyToDirectory(VfsUtil.findFileByIoFile(projectRoot, true));

                return projectName;
            }
        }
    }

    @Override
    protected void doRunWizard() {
        VirtualFile projectRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(this.adapter.getLocation());
        if (projectRoot == null) {
            return;
        }

        refreshProjectDir(projectRoot);
        final VirtualFile cMakeLists = projectRoot.findChild("CMakeLists.txt");
        if (cMakeLists == null) {
            return;
        }

        final VirtualFile mainSketchFile = projectRoot.findChild(this.adapter.getName() + ".ino");
        if (mainSketchFile == null) {
            return;
        }

        final Project project;
        try {
            project = ProjectManager.getInstance().loadAndOpenProject(cMakeLists.getParent().getPath());
        } catch (IOException | JDOMException e) {
            CPPLog.LOG.warn(e);
            return;
        }

        if (project == null) {
            return;
        }

        CMakeProjectOpenProcessor.OpenProjectSpec projectSpec = CMakeProjectOpenProcessor.getHelper()
                .getAndClearFileToOpenData(project);

        deleteBuildOutputDir(projectSpec);

        (new OpenFileDescriptor(project, cMakeLists)).navigate(false);
        (new OpenFileDescriptor(project, mainSketchFile)).navigate(true);

        // vsch: Need to load the CMakeList.txt to generate the project files, otherwise it appears empty
        CMakeWorkspace workspace = CMakeWorkspace.getInstance(project);

        //// vsch: can use this code open files only after project is loaded, tested in 2018.3
        //MessageBusConnection busConnection = project.getMessageBus().connect();
        //busConnection.subscribe(CMakeWorkspaceListener.TOPIC, new CMakeWorkspaceListener() {
        //    @Override
        //    public void reloadingFinished(final boolean canceled) {
        //        busConnection.disconnect();
        //
        //        if (!canceled) {
        //            (new OpenFileDescriptor(project, cMakeLists)).navigate(false);
        //            (new OpenFileDescriptor(project, mainSketchFile)).navigate(true);
        //        }
        //    }
        //});

        // vsch: bring new project into focus and open project tool window
        final ToolWindowManagerEx manager = (ToolWindowManagerEx) ToolWindowManager.getInstance(project);
        final ToolWindow toolWindow = manager.getToolWindow(ToolWindowId.PROJECT_VIEW);
        if (toolWindow != null) {
            if (toolWindow.getType() != ToolWindowType.SLIDING) {
                toolWindow.activate(null);
            }
        }

        // vsch: start the loading if method exists (2018.3 and probably 2018.1)
        try {
            //workspace.selectProjectDir(cmakeLists);
            Method method = CMakeWorkspace.class.getMethod("selectProjectDir", File.class);
            File cmakeLists = VfsUtilCore.virtualToIoFile(cMakeLists.getParent());
            method.invoke(workspace, cmakeLists);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    private static void deleteBuildOutputDir(CMakeProjectOpenProcessor.OpenProjectSpec projectSpec) {
        if (projectSpec != null && projectSpec.generationDir != null) {
            FileUtil.delete(projectSpec.generationDir);
        }
    }
}
