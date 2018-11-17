package com.vladsch.clionarduinoplugin.serial;

import jssc.SerialPortEvent;

public interface SerialPortListener {
    default void onConnect(final String portName, final int baudRate) {};
    default void onDisconnect(final String portName, final int baudRate) {};

    default void onReceive(final byte[] buf) {};
    default void onSent(final byte[] buf) {};
    default void onEvent(SerialPortEvent event) {};
}
