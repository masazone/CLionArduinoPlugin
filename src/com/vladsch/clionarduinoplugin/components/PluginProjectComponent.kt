package com.vladsch.clionarduinoplugin.components

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.*
import com.intellij.util.Alarm
import com.intellij.util.messages.MessageBusConnection
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspaceListener
import com.vladsch.clionarduinoplugin.Bundle
import com.vladsch.clionarduinoplugin.generators.ArduinoProjectGeneratorBase
import com.vladsch.clionarduinoplugin.resources.Strings
import java.io.File

class PluginProjectComponent(val project: Project) : ProjectComponent, VirtualFileListener, CMakeWorkspaceListener, Disposable {

    private var inReload = false
    private lateinit var busConnection: MessageBusConnection
    private val alarm = Alarm()
    private lateinit var settings: ArduinoProjectSettings
    private lateinit var projectDir: String

    override fun getComponentName(): String {
        return Bundle.message("plugin.project-component.name")
    }

    override fun dispose() {
        busConnection.dispose()
    }

    override fun disposeComponent() {
    }

    override fun projectClosed() {
        busConnection.disconnect()
        VirtualFileManager.getInstance().removeVirtualFileListener(this)
    }

    override fun initComponent() {
    }

    override fun projectOpened() {
        settings = ArduinoProjectSettings.getInstance(project)

        val workspace = CMakeWorkspace.getInstance(project)
        busConnection = project.messageBus.connect(this)
        busConnection.subscribe(CMakeWorkspaceListener.TOPIC, this)

        projectDir = workspace.projectDir.path + File.separator

        VirtualFileManager.getInstance().addVirtualFileListener(this, this)
    }

    private fun triggerReload(event: VirtualFileEvent) {
        if (!inReload && settings.isReloadOnFileChange) {
            if ((event.file.path + File.separator).startsWith(projectDir) && event.file.extension.equals(Strings.INO_EXT, true)) {
                inReload = true
                if (alarm.isDisposed) return

                alarm.cancelAllRequests()
                alarm.addRequest({ ArduinoProjectGeneratorBase.reloadCMakeLists(project) }, 500)
            }
        }
    }

    override fun fileDeleted(event: VirtualFileEvent) {
        triggerReload(event)
    }

    override fun fileMoved(event: VirtualFileMoveEvent) {
        triggerReload(event)
    }

    override fun fileCreated(event: VirtualFileEvent) {
        triggerReload(event)
    }

    override fun fileCopied(event: VirtualFileCopyEvent) {
        triggerReload(event)
    }

    override fun reloadingStarted() {
        inReload = true
        alarm.cancelAllRequests()
    }

    override fun reloadingFinished(canceled: Boolean) {
        inReload = false
        alarm.cancelAllRequests()
    }

    override fun reloadingRescheduled() {
        inReload = true
        alarm.cancelAllRequests()
    }
}
