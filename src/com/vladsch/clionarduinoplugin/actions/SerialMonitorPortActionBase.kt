package com.vladsch.clionarduinoplugin.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Toggleable
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings
import com.vladsch.clionarduinoplugin.util.Utils

class SerialMonitorPortActionBase(internal val myPort: String) : AnAction(myPort), Toggleable {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val projectSettings = ArduinoProjectSettings.getInstance(project)
            projectSettings.groupChanges {
                projectSettings.port = myPort
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val port = ArduinoProjectSettings.getInstance(project).port
            val presentation = e.presentation
            presentation.putClientProperty(Toggleable.SELECTED_PROPERTY, port == myPort)
            if (e.isFromContextMenu) {
                //force to show check marks instead of toggled icons in context menu
                presentation.icon = null
            }
        }
    }

    companion object {
        fun createSerialPortsActionGroup(): ActionGroup {
            val serialPorts = Utils.getSerialPorts(true)
            val portActions = serialPorts.map { SerialMonitorPortActionBase(it) }

            val group = object : ActionGroup("Port", true) {
                override fun getChildren(e: AnActionEvent?): Array<AnAction> {
                    return portActions.toTypedArray()
                }
            }
            return group
        }
    }
}
