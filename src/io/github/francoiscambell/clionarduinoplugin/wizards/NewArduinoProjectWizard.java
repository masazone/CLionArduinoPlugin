package io.github.francoiscambell.clionarduinoplugin.wizards;

import com.intellij.ide.RecentDirectoryProjectsManager;
import com.intellij.ide.RecentProjectsManager;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.impl.stores.DirectoryStorageUtil;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.cidr.cpp.CPPLog;
import com.jetbrains.cidr.cpp.cmake.CMakeProjectOpenProcessor;
import com.jetbrains.cidr.cpp.cmake.projectWizard.*;
import io.github.francoiscambell.clionarduinoplugin.CMakeListsEditor;
import io.github.francoiscambell.clionarduinoplugin.resources.ArduinoToolchainFiles;
import io.github.francoiscambell.clionarduinoplugin.resources.Strings;
import org.jdom.JDOMException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
                cMakeListsEditor.project("${PROJECT_NAME}");
                cMakeListsEditor.blankLine();
                cMakeListsEditor.set("${CMAKE_PROJECT_NAME}_SKETCH", projectName + ".ino");
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
            project = ProjectManager.getInstance().loadAndOpenProject(cMakeLists.getPath());
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
    }

    private void deleteBuildOutputDir(CMakeProjectOpenProcessor.OpenProjectSpec projectSpec) {
        if (projectSpec != null && projectSpec.generationDir != null) {
            FileUtil.delete(projectSpec.generationDir);
        }
    }
}
