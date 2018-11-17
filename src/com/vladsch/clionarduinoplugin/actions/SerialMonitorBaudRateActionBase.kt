package com.vladsch.clionarduinoplugin.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Toggleable
import com.vladsch.clionarduinoplugin.components.ArduinoProjectSettings
import jssc.SerialPort

class SerialMonitorBaudRateActionBase(internal val myBaudRate: Int) : AnAction(Integer.toString(myBaudRate)), Toggleable {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val projectSettings = ArduinoProjectSettings.getInstance(project)
            projectSettings.baudRate = myBaudRate
            projectSettings.fireSettingsChanged();
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val baudRate = ArduinoProjectSettings.getInstance(project).baudRate
            val presentation = e.presentation
            presentation.putClientProperty(Toggleable.SELECTED_PROPERTY, baudRate == myBaudRate)
            if (e.isFromContextMenu) {
                //force to show check marks instead of toggled icons in context menu
                presentation.icon = null
            }
        }
    }

    companion object {
        fun createBaudRateActionGroup(): ActionGroup {
            // TODO: add to string properties
            val group = object : ActionGroup("Baud rate",true) {
                override fun getChildren(e: AnActionEvent?): Array<AnAction> {
                    return arrayOf(
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_110),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_300),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_600),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_1200),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_4800),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_9600),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_14400),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_19200),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_38400),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_57600),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_115200),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_128000),
                            SerialMonitorBaudRateActionBase(SerialPort.BAUDRATE_256000)
                    )
                }
            }
            return group
        }
    }
}
