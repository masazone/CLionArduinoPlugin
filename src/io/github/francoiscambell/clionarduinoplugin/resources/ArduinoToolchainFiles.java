package io.github.francoiscambell.clionarduinoplugin.resources;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francois on 15-08-03.
 */
public class ArduinoToolchainFiles {
    public static InputStream getArduinoToolchainCmake() {
        return ArduinoToolchainFiles.class.getResourceAsStream("arduino-cmake/cmake/ArduinoToolchain.cmake");
    }

    public static InputStream getArduinoCmake() {
        return ArduinoToolchainFiles.class.getResourceAsStream("arduino-cmake/cmake/Platform/Arduino.cmake");
    }

    public static VirtualFile[] copyToDirectory(final VirtualFile projectRoot) {
        ArrayList<VirtualFile> files = new ArrayList<>();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {

            @Override
            public void run() {
                try {
                    VirtualFile cmakeDirectory = projectRoot.createChildDirectory(this, "cmake");
                    VirtualFile platformDirectory = cmakeDirectory.createChildDirectory(this, "Platform");

                    VirtualFile arduinoToolchain = cmakeDirectory.createChildData(this, "ArduinoToolchain.cmake");
                    VirtualFile arduino = platformDirectory.createChildData(this, "Arduino.cmake");


                    OutputStream arduinoToolchainOutputStream = arduinoToolchain.getOutputStream(this);
                    OutputStream arduinoOutputStream = arduino.getOutputStream(this);

                    InputStream arduinoToolchainInputStream = getArduinoToolchainCmake();
                    InputStream arduinoInputStream = getArduinoCmake();

                    try {
                        IOUtils.copy(arduinoToolchainInputStream, arduinoToolchainOutputStream);
                        IOUtils.copy(arduinoInputStream, arduinoOutputStream);
                        files.add(arduinoToolchain);
                        files.add(arduino);
                    } finally {
                        closeStreams(arduinoToolchainOutputStream,
                                arduinoOutputStream,
                                arduinoToolchainInputStream,
                                arduinoInputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return files.toArray(new VirtualFile[0]);
    }

    static void closeStreams(Closeable... streams) {
        for (Closeable c : streams) {
            IOUtils.closeQuietly(c);
        }
    }
}
