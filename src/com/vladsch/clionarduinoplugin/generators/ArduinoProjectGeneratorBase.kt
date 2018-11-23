/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators

import com.intellij.facet.ui.ValidationResult
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.util.messages.MessageBusConnection
import com.jetbrains.cidr.cpp.CPPLog
import com.jetbrains.cidr.cpp.cmake.projectWizard.CLionProjectWizardUtils
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeProjectGenerator
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.ui.CMakeSettingsPanel
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspaceListener
import com.vladsch.clionarduinoplugin.Bundle
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.resources.ArduinoToolchainFiles
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.clionarduinoplugin.settings.NewProjectSettingsForm
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener
import com.vladsch.clionarduinoplugin.util.Utils
import icons.PluginIcons
import org.jetbrains.annotations.Nls
import java.awt.BorderLayout
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

abstract class ArduinoProjectGeneratorBase(protected val myIsLibrary: Boolean) : CMakeProjectGenerator(), Disposable {
    protected val mySettings: ArduinoApplicationSettings = ArduinoApplicationSettings.getInstance().state
    private val myListeners = HashSet<WeakReference<GeneratorFailedValidationListener>>()

    override fun dispose() {
    }

    override fun getGroupName(): String {
        return Bundle.message("new-project.group.name")
    }

    @Nls
    abstract override fun getName(): String

    override fun getLogo(): Icon? {
        return PluginIcons.arduino_logo
    }

    override fun getCMakeFileContent(projectName: String): String {
        return ""
    }

    override fun getLanguageVersion(): String {
        return mySettings.languageVersion
    }

    override fun getLibraryType(): String? {
        return mySettings.libraryType
    }

    override fun setLanguageVersion(languageVersion: String) {
        mySettings.languageVersion = languageVersion
    }

    override fun setLibraryType(libraryType: String?) {
        mySettings.setLibraryType(libraryType)
    }

    override fun getLanguageVersions(): Array<String> {
        return ArduinoApplicationSettings.LANGUAGE_VERSIONS
    }

    protected fun getCMakeFileContent(projectName: String, sourceFiles: Array<VirtualFile>): String {
        val sb = LineStringBuilder("# ")
        val isStaticLib = myIsLibrary && "static" == mySettings.libraryType

        val boardId = Utils.ifNullOrEmpty(mySettings.boardId, "uno")

        sb.appendln("cmake_minimum_required(VERSION 2.8.4)")
        sb.appendln("set(CMAKE_TOOLCHAIN_FILE \${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)")
        val languageVersion = CppLanguageVersions.fromDisplayString(languageVersion)
        if (!languageVersion.isEmpty()) {
            sb.appendln("set(CMAKE_CXX_STANDARD $languageVersion)")
        }
        sb.line()

        sb.appendln("set(PROJECT_NAME $projectName)")

        sb.appendln("set(\${CMAKE_PROJECT_NAME}_BOARD $boardId)")
        val cpu = mySettings.cpuId
        sb.prefixNullOrEmpty(cpu).appendln("set(ARDUINO_CPU " + Utils.ifNullOrEmpty(cpu, "8MHzatmega328") + ")")
        sb.appendln("project(\${PROJECT_NAME})")
        sb.line()

        sb.appendln("# Define the source code for cpp files or default arduino sketch files")
        val cppFiles = StringBuilder()
        val hFiles = StringBuilder()
        val sep = ""
        var sketchFile: String? = null

        for (file in sourceFiles) {
            val ext = file.extension
            if (ext != null) {
                if (ext.equals("c", ignoreCase = true) || ext.equals(Strings.CPP_EXT, ignoreCase = true)) {
                    cppFiles.append(" ").append(file.name)
                } else if (ext.equals("hpp", ignoreCase = true) || ext.equals(Strings.H_EXT, ignoreCase = true)) {
                    hFiles.append(" ").append(file.name)
                } else if (ext.equals(Strings.INO_EXT, ignoreCase = true) || ext.equals(Strings.PDE_EXT, ignoreCase = true)) {
                    sketchFile = file.name
                }
            }
        }

        if (cppFiles.isNotEmpty()) {
            sb.appendln("set(\${PROJECT_NAME}_SRCS " + cppFiles.toString() + ")")
        } else {
            sb.appendln("# set(\${PROJECT_NAME}_SRCS " + projectName + Strings.DOT_CPP_EXT + ")")
        }

        if (hFiles.isNotEmpty()) {
            sb.appendln("set(\${PROJECT_NAME}_HDRS " + hFiles.toString() + ")")
        }

        sb.appendln("### Additional static libraries to include in the target.")
        sb.appendln("# set(\${CMAKE_PROJECT_NAME}_LIBS lib_name)")
        sb.line()

        if (sketchFile != null) {
            sb.appendln("set(\${CMAKE_PROJECT_NAME}_SKETCH $sketchFile)")
        } else {
            sb.appendln("# set(\${CMAKE_PROJECT_NAME}_SKETCH " + projectName + Strings.DOT_INO_EXT + ")")
        }

        sb.line()

        if (mySettings.isAddLibraryDirectory && !mySettings.libraryDirectory.isEmpty()) {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.")
            sb.appendln("# An Arduino library my_lib will contain files in " + mySettings.libraryDirectory + "/my_lib/: my_lib.h, my_lib.cpp + any other cpp files")
            sb.appendln("link_directories(\${CMAKE_CURRENT_SOURCE_DIR}/" + mySettings.libraryDirectory + ")")
            sb.line()
        } else {
            sb.appendln("### Additional settings to add non-standard or your own Arduino libraries.")
            sb.appendln("# For this example (libs will contain additional arduino libraries)")
            sb.appendln("# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files")
            sb.prefix().appendln("link_directories(\${CMAKE_CURRENT_SOURCE_DIR}/libs)")
            sb.line()
        }

        if (!myIsLibrary) {

            if (sketchFile != null) {
                sb.appendln("# For nested library sources replace \${LIB_NAME} with library name for each library")
                sb.prefix().appendln("set(\${LIB_NAME}_RECURSE true)")
                sb.line()
            }
        }

        sb.appendln("#### Additional settings for programmer. From programmers.txt")
        val programmer = mySettings.programmerId
        sb.prefix(programmer).appendln("set(\${CMAKE_PROJECT_NAME}_PROGRAMMER " + Utils.ifNullOrEmpty(programmer, "avrispmkii") + ")")
        sb.prefixNullOrEmpty(mySettings.port).appendln("set(\${CMAKE_PROJECT_NAME}_PORT " + Utils.ifNullOrEmpty(mySettings.port, "/dev/cu.usbserial-00000000") + ")")
        if (mySettings.baudRate > 0) {
            sb.prefix().appendln(String.format("set(%s.upload.speed %s)", boardId, Utils.ifNullOrEmpty(mySettings.baudRateText, "9600")))
        } else {
            sb.prefix().appendln("set(pro.upload.speed 9600)")
        }
        sb.line()
        sb.appendln("## Verbose build process")
        sb.prefix(!mySettings.isVerbose).appendln("set(\${CMAKE_PROJECT_NAME}_AFLAGS -v)")
        sb.line()

        if (isStaticLib) {
            sb.appendln("generate_arduino_library(\${CMAKE_PROJECT_NAME})")
        } else {
            sb.appendln("generate_arduino_firmware(\${CMAKE_PROJECT_NAME})")
        }
        return sb.toString()
    }

    @Throws(IOException::class)
    abstract override fun createSourceFiles(name: String, dir: VirtualFile): Array<VirtualFile>

    override fun validate(baseDirPath: String): ValidationResult {
        if (StringUtil.isEmptyOrSpaces(baseDirPath)) {
            return ValidationResult("Enter project location")
        }
        val baseDir = File(baseDirPath)
        if (!baseDir.isAbsolute) {
            return ValidationResult("Project location path should be absolute")
        } else if (baseDir.exists() && !baseDir.canWrite()) {
            return ValidationResult(String.format("Directory '%s' is not writable.\nPlease choose another directory.", baseDirPath))
        } else {
            // validate other fields, but only if the location text field was found so we can trigger another validation
            if (mySettings.boardId.isEmpty()) {
                return filterFailure(ValidationResult(Bundle.message("new-project.no-board")))
            }

            if (mySettings.getBoardCpuNames(mySettings.boardName).isNotEmpty() && mySettings.cpuId.isEmpty()) {
                return filterFailure(ValidationResult(Bundle.message("new-project.1.no-cpu", mySettings.cpuLabel, mySettings.boardName)))
            }

            if (mySettings.isAddLibraryDirectory) {
                if (mySettings.libraryDirectory.startsWith("/")) {
                    return filterFailure(ValidationResult(String.format("Library sub-directory '%s' must be relative to project path.", mySettings.libraryDirectory)))
                }
            }
        }
        return super.validate(baseDirPath)
        //return ValidationResult.OK;
    }

    private fun filterFailure(result: ValidationResult): ValidationResult {
        val canFail = fireValidationFailed()
        return if (canFail) result else ValidationResult.OK
        // here we have a problem, we'll use defaults and hope for the best
    }

    override fun generateProject(project: Project, baseDir: VirtualFile, settings: CMakeProjectSettings, module: Module) {
        val createdFilesHolder: CreatedFilesHolder
        try {
            createdFilesHolder = ApplicationManager.getApplication().runWriteAction(ThrowableComputable<CreatedFilesHolder, IOException> {
                createFiles(project.name, baseDir)
            })
        } catch (e: IOException) {
            handleErrorDuringGeneration(project, e)
            return
        }

        CLionProjectWizardUtils.reformatProjectFiles(project, createdFilesHolder.cMakeFile, formatSourceFilesAsCpp(), *createdFilesHolder.sourceFiles)
        CMakeWorkspace.getInstance(project).selectProjectDir(VfsUtilCore.virtualToIoFile(baseDir))
        if (!ApplicationManager.getApplication().isHeadlessEnvironment) {
            PsiNavigationSupport.getInstance().createNavigatable(project, createdFilesHolder.cMakeFile, -1).navigate(false)
            Arrays.asList(*createdFilesHolder.sourceFiles).forEach { file -> PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true) }
        }

        // vsch: Need to reload the CMakeList.txt to generate build files, first time generation is incorrect
        val workspace = CMakeWorkspace.getInstance(project)

        val busConnection = project.messageBus.connect()
        busConnection.subscribe(CMakeWorkspaceListener.TOPIC, MyCMakeWorkspaceListener(busConnection, workspace))
    }

    private class MyCMakeWorkspaceListener(private val myBusConnection: MessageBusConnection, private val myWorkspace: CMakeWorkspace) : CMakeWorkspaceListener {

        override fun reloadingFinished(canceled: Boolean) {
            myBusConnection.disconnect()

            if (!canceled) {
                ApplicationManager.getApplication().invokeLater {
                    // force reload after the first generation cycle is complete
                    myWorkspace.selectProjectDir(myWorkspace.projectDir)
                }
            }
        }
    }

    override fun handleErrorDuringGeneration(project: Project, e: Exception) {
        Messages.showErrorDialog(project, "Cannot create a new project: " + e.message, "New Project")
        CPPLog.LOG.info(e)
    }

    override fun createPeer(): ProjectGeneratorPeer<CMakeProjectSettings> {
        return GeneratorPeerImpl(mySettings, settingsPanel)
    }

    override fun getSettingsPanel(): JComponent {
        return ArduinoNewProjectSettingsPanel(mySettings, this)
    }

    internal interface GeneratorFailedValidationListener {
        fun onValidationFailed(): Boolean
    }

    private fun addValidationListener(listener: GeneratorFailedValidationListener) {
        myListeners.add(WeakReference(listener))
    }

    private fun fireValidationFailed(): Boolean {
        var canFail = false
        val invalidListeners = ArrayList<WeakReference<GeneratorFailedValidationListener>>(myListeners.size)
        for (ref in myListeners) {
            val listener = ref.get()
            if (listener != null) {
                if (listener.onValidationFailed()) {
                    canFail = true
                } else {
                    invalidListeners.add(ref)
                }
            } else {
                invalidListeners.add(ref)
            }
        }

        myListeners.removeAll(invalidListeners)
        return canFail
    }

    private class ArduinoNewProjectSettingsPanel(val settings: ArduinoApplicationSettings, projectGenerator: ArduinoProjectGeneratorBase) : CMakeSettingsPanel(projectGenerator), ApplicationSettingsListener, GeneratorFailedValidationListener, Disposable {
        private val myPanel: JPanel
        private var myHaveFailed = false

        private//.getParent();
        val locationTextField: JTextField?
            get() {
                try {
                    val basePanel = myPanel.parent.parent.parent as JPanel
                    return findLocationTextField(basePanel)
                } catch (ignored: Throwable) {
                    return null
                }
            }

        init {
            val form = NewProjectSettingsForm(settings, projectGenerator.myIsLibrary, true)

            layout = BorderLayout()

            val layoutManager = GridLayoutManager(1, 1)
            myPanel = JPanel(layoutManager)

            val constraints = GridConstraints()
            constraints.row = 0
            constraints.column = 0
            constraints.anchor = 8
            constraints.fill = GridConstraints.FILL_HORIZONTAL
            myPanel.add(form.component, constraints)

            add(myPanel, "Center")

            ApplicationManager.getApplication().messageBus.connect(this).subscribe(ApplicationSettingsListener.TOPIC, this)
            projectGenerator.addValidationListener(this)
        }

        override fun onValidationFailed(): Boolean {
            myHaveFailed = locationTextField != null
            return myHaveFailed
        }

        override fun onSettingsChanged() {
            if (myHaveFailed) {
                myHaveFailed = false
                val textField = locationTextField
                if (textField != null) {
                    textField.text = textField.text
                }
            }
        }

        // vsch: kludge: since this class is not a step in the wizard process it cannot trigger re-validation,
        // or at least I wasn't able to figure out how to do it. Workaround: find the location text field
        // and set it to itself to trigger validation to clear the error
        private fun findLocationTextField(jcomp: JComponent): JTextField? {
            var field: JTextField?
            val iMax = jcomp.componentCount

            for (i in 0 until iMax) {
                val comp = jcomp.getComponent(i)
                if (comp is JComponent) {
                    if (jcomp is TextFieldWithBrowseButton) {
                        field = jcomp.textField
                        return field
                    }
                    field = findLocationTextField(comp)
                    if (field != null) return field
                }
            }
            return null
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

        override fun dispose() {
        }
    }

    @Throws(IOException::class)
    protected fun createFiles(projectName: String, rootDir: VirtualFile): CreatedFilesHolder {
        val sanitizedName = FileUtil.sanitizeFileName(projectName)
        val sourceFiles = createSourceFiles(sanitizedName, rootDir)

        val cMakeFile = createCMakeFile(sanitizedName, rootDir, sourceFiles)

        val extraFiles = ArduinoToolchainFiles.copyToDirectory(VfsUtil.findFileByIoFile(VfsUtilCore.virtualToIoFile(rootDir), false))
        if (mySettings.isAddLibraryDirectory) {
            val libDir = File(rootDir.path + "/" + mySettings.libraryDirectory)
            if (!libDir.exists() && libDir.canonicalPath != rootDir.canonicalPath) {
                libDir.mkdirs()
            }
        }
        return CreatedFilesHolder(cMakeFile, sourceFiles, extraFiles)
    }

    @Throws(IOException::class)
    protected fun createCMakeFile(name: String, dir: VirtualFile, sourceFiles: Array<VirtualFile>): VirtualFile {
        return createProjectFileWithContent(dir, "CMakeLists.txt", getCMakeFileContent(name, sourceFiles))
    }

    override fun formatSourceFilesAsCpp(): Boolean {
        return false
    }

    protected class CreatedFilesHolder constructor(val cMakeFile: VirtualFile, val sourceFiles: Array<VirtualFile>, val extraFiles: Array<VirtualFile>)
}
