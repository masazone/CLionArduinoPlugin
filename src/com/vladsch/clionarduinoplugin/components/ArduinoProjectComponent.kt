package com.vladsch.clionarduinoplugin.components

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.util.messages.MessageBusConnection
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspaceListener
import com.vladsch.clionarduinoplugin.Bundle
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings
import java.util.concurrent.atomic.AtomicBoolean

class ArduinoProjectComponent(val project: Project) : ProjectComponent, VirtualFileListener, CMakeWorkspaceListener, Disposable {
    companion object {
        fun getInstance(project: Project): ArduinoProjectComponent {
            return project.getComponent(ArduinoProjectComponent::class.java)
        }
    }

    var inReload = AtomicBoolean(false)
        private set

    private lateinit var myBusConnection: MessageBusConnection
    //    private val alarm = Alarm()
    private lateinit var mySettings: ArduinoProjectSettings
    //    private val myRunnables = ListenersRunner<File>()
    
    var isArduinoProject: Boolean? = null

    override fun getComponentName(): String {
        return Bundle.message("plugin.project-component.name")
    }

    override fun dispose() {
    }

    override fun disposeComponent() {
        myBusConnection.dispose()
    }

    override fun projectClosed() {
        myBusConnection.disconnect()
        //        VirtualFileManager.getInstance().removeVirtualFileListener(this)
    }

    override fun initComponent() {
    }

    private val workspace = CMakeWorkspace.getInstance(project)

    override fun projectOpened() {
        mySettings = ArduinoProjectSettings.getInstance(project)
        myBusConnection = project.messageBus.connect(this)
        myBusConnection.subscribe(CMakeWorkspaceListener.TOPIC, this)

        //        myProjectDir = workspace.projectDir.path + File.separator
        //        VirtualFileManager.getInstance().addVirtualFileListener(this, this)
    }

    override fun beforeApplying() {
        inReload.set(true)
        isArduinoProject = null
    }

    override fun generationStarted() {
        inReload.set(true)
        isArduinoProject = null
    }

    override fun filesRefreshedAfterGeneration() {
        val tmp = 0
    }

    override fun generationFinished() {
        val tmp = 0
    }

    override fun reloadingStarted() {
        inReload.set(true)
        isArduinoProject = null
        //        alarm.cancelAllRequests()
    }

    override fun reloadingFinished(canceled: Boolean) {
        inReload.set(false)
        isArduinoProject = null
        //        alarm.cancelAllRequests()
    }

    override fun reloadingRescheduled() {
        inReload.set(true)
        isArduinoProject = null
        //        alarm.cancelAllRequests()
    }

    //    private fun triggerReload(event: VirtualFileEvent) {
    //        if (!myInReload && mySettings.isReloadOnFileChange) {
    //            if ((event.file.path + File.separator).startsWith(myProjectDir) && event.file.extension.equals(Strings.INO_EXT, true)) {
    //                myInReload = true
    //                if (alarm.isDisposed) return
    //
    //                alarm.cancelAllRequests()
    //                alarm.addRequest({ ArduinoProjectGeneratorBase.reloadCMakeLists(project) }, 500)
    //            }
    //        }
    //    }

    //    override fun fileDeleted(event: VirtualFileEvent) {
    //        triggerReload(event)
    //    }
    //
    //    override fun fileMoved(event: VirtualFileMoveEvent) {
    //        triggerReload(event)
    //    }
    //
    //    override fun fileCreated(event: VirtualFileEvent) {
    //        triggerReload(event)
    //    }
    //
    //    override fun fileCopied(event: VirtualFileCopyEvent) {
    //        triggerReload(event)
    //    }
}
