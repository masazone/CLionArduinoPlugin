package com.vladsch.clionarduinoplugin.serial;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.execution.CidrBuildConfiguration;
import com.jetbrains.cidr.execution.build.CidrBuildEvent;
import com.jetbrains.cidr.execution.build.CidrBuildListener;
import com.jetbrains.cidr.execution.build.CidrBuildResult;
import com.vladsch.clionarduinoplugin.components.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.util.Utils;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SerialProjectComponent implements ProjectComponent, CidrBuildListener {
    private static final Logger LOG = Logger.getInstance("com.vladsch.clionarduinoplugin.serial");

    final Project myProject;
    final StatusWidget myStatusWidget;
    final @NotNull ArduinoProjectSettings myProjectSettings;
    MessageBusConnection myConnection;
    boolean myIsBuilding = false;
    boolean myWasConnected;
    String myConnectedPort;
    int myConnectedBaudRate;
    private SerialPortManager myPortManager;
    SerialMonitorToolWindow mySerialMonitorToolWindow;

    SerialPort mySerialPort;
    final Set<SerialPortListener> myListeners = Collections.synchronizedSet(new HashSet<SerialPortListener>());

    public SerialProjectComponent(final Project project) {
        myProject = project;
        myStatusWidget = new StatusWidget(this);
        myProjectSettings = ArduinoProjectSettings.getInstance(myProject);
    }

    @Override
    public void projectOpened() {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
        statusBar.addWidget(myStatusWidget, "before Position");
        MessageBus messageBus = myProject.getMessageBus();
        myConnection = messageBus.connect(myProject);
        //myConnection = ApplicationManager.getApplication().getMessageBus().connect();
        myConnection.subscribe(CidrBuildListener.TOPIC, this);
        myStatusWidget.setStatus(true, false, canConnectPort());
        mySerialMonitorToolWindow = new SerialMonitorToolWindow(myProject);
    }

    @Override
    public void projectClosed() {
        //StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
        //statusBar.removeWidget(myStatusWidget.ID());
        if (mySerialMonitorToolWindow != null) {
            mySerialMonitorToolWindow.unregisterToolWindow();
            mySerialMonitorToolWindow = null;
        }

        disconnectPort();

        myStatusWidget.dispose();
        myConnection.disconnect();
        myConnection.dispose();
        myListeners.clear();

        SerialPortManager portManager = ApplicationManager.getApplication().getComponent(SerialPortManager.class);
        portManager.removeProjectComponent(this);
    }

    @Override
    public void initComponent() {
        myPortManager = SerialPortManager.getInstance();
    }

    @Override
    public void disposeComponent() {
        disconnectPort();
        myPortManager = null;
    }

    @Override
    public void beforeStarted(@NotNull final CidrBuildEvent buildEvent) {
        CidrBuildConfiguration configuration = buildEvent.getBuildConfiguration();
        String name = configuration.getName();
        if (configuration instanceof CMakeConfiguration) {
            CMakeConfiguration makeConfiguration = (CMakeConfiguration) configuration;
            String targetName = makeConfiguration.getTarget().getName();
            name = targetName;
        }

        if (myProjectSettings.isDisconnectOnBuild() && myProjectSettings.isBuildConfigurationMatched(name)) {
            boolean wasConnected = isPortConnected();
            myIsBuilding = true;
            myPortManager.disconnectPort(myProjectSettings.getPort());
            myWasConnected = wasConnected;
        } else {
            myWasConnected = false;
        }
        myStatusWidget.setStatus(false, myConnectedPort != null, myConnectedPort != null, "In Build: " + name);
    }

    @Override
    public void afterFinished(@NotNull final CidrBuildEvent buildEvent, @NotNull final CidrBuildResult result) {
        myIsBuilding = false;
        if (myWasConnected && myProjectSettings.isReconnectAfterBuild()) {
            if (!myProjectSettings.isAfterSuccessfulBuild() || result.getSucceeded()) {
                connectPort(myProjectSettings.getPort(), myProjectSettings.getBaudRate());
            }
        }
        myStatusWidget.setStatus(true, myConnectedPort != null, canConnectPort());
    }

    public boolean isPortConnected() {
        return myConnectedPort != null;
    }

    public boolean connectPort(@NotNull String port, int baudRate) {
        if (!isPortConnected() && !myIsBuilding) {
            myPortManager.disconnectPort(port);

            connect(port, baudRate);

            if (mySerialPort != null) {
                // once connected
                myConnectedPort = port;
                myConnectedBaudRate = baudRate;
                myPortManager.setPortOwner(this, myConnectedPort);
                myStatusWidget.setStatus(true, true, true);

                if (myProjectSettings.isActivateOnConnect()) {
                    // active tool window
                    mySerialMonitorToolWindow.activate();
                }
            }
        }
        return true;
    }

    public boolean connectPort() {
        return connectPort(myProjectSettings.getPort(), myProjectSettings.getBaudRate());
    }

    public boolean isBuilding() {
        return myIsBuilding;
    }

    public boolean isWasConnected() {
        return myWasConnected;
    }

    public String getConnectedPort() {
        return myConnectedPort;
    }

    public int getConnectedBaudRate() {
        return myConnectedBaudRate;
    }

    public void disconnectPort() {
        if (myConnectedPort != null) {
            if (mySerialPort != null) {
                try {
                    if (mySerialPort.isOpened()) {
                        mySerialPort.removeEventListener();
                        mySerialPort.closePort();
                    }
                } catch (SerialPortException e) {
                    LOG.error("Error closing port", e);
                }
            }

            final String port = myConnectedPort;
            final int baud = myConnectedBaudRate;
            mySerialPort = null;
            myWasConnected = false;
            myPortManager.removeProjectComponent(this);

            for (SerialPortListener listener : myListeners) {
                listener.onDisconnect(port, baud);
            }

            myConnectedPort = null;
            if (!myIsBuilding) {
                myStatusWidget.setStatus(true, false, canConnectPort());
            }
        }
    }

    public void disconnectPort(@NotNull final String port) {
        if (port.equals(myConnectedPort)) {
            disconnectPort();
        }
    }

    public boolean canConnectPort() {
        return !myIsBuilding && Utils.getSerialPorts(false).contains(myProjectSettings.getPort());
    }

    public Project getProject() {
        return myProject;
    }

    @Override
    public @NotNull String getComponentName() {
        return "Arduino Support Terminal Component";
    }

    public static SerialProjectComponent getInstance(@NotNull Project project) {
        return project.getComponent(SerialProjectComponent.class);
    }

    private void connect(String portName, int baudRate) {
        int dataBits = SerialPort.DATABITS_8;
        int stopBits = SerialPort.STOPBITS_1;
        int parity = SerialPort.PARITY_NONE;

        try {
            mySerialPort = new SerialPort(portName);
            mySerialPort.openPort();
            if (!mySerialPort.setParams(baudRate, dataBits, stopBits, parity, true, true)) {
                // TODO: signal error
                // Failed to set parameters
                mySerialPort = null;
            } else {
                mySerialPort.addEventListener(new SerialPortEventListener() {
                    @Override
                    public synchronized void serialEvent(SerialPortEvent serialEvent) {
                        if (serialEvent.isRXCHAR()) {
                            if (myListeners.isEmpty()) {
                                return;
                            }
                            try {
                                byte[] buf = mySerialPort.readBytes(serialEvent.getEventValue());
                                if (buf.length > 0) {
                                    for (SerialPortListener listener : myListeners) {
                                        listener.onReceive(buf);
                                    }
                                }
                            } catch (SerialPortException e) {
                                LOG.error(e);
                            }
                        } else {
                            for (SerialPortListener listener : myListeners) {
                                listener.onEvent(serialEvent);
                            }
                        }
                    }
                });

                for (SerialPortListener listener : myListeners) {
                    listener.onConnect(portName, baudRate);
                }
            }
        } catch (SerialPortException e) {
            mySerialPort = null;
            LOG.error("Error opening port", e);
        }
    }

    public void send(byte[] bytes) {
        if (isPortConnected()) {
            try {
                mySerialPort.writeBytes(bytes);
                return;
            } catch (SerialPortException e) {
                LOG.error("Send serial port error", e);
            }
        }

        throw new IllegalStateException("Serial Port is not connected");
    }

    public void addPortListener(@NotNull SerialPortListener listener) {
        myListeners.add(listener);
    }

    public void removeListener(SerialPortListener listener) {
        myListeners.remove(listener);
    }
}
