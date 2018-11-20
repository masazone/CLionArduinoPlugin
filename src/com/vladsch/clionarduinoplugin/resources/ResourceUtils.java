package com.vladsch.clionarduinoplugin.resources;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class ResourceUtils {
    public static String getFileContent(File file) {
        try {
            StringWriter writer = new StringWriter();
            InputStream inputStream = new FileInputStream(file);
            getStreamContent(writer, inputStream);
            return writer.toString();
        } catch (FileNotFoundException ignored) {
            //e.printStackTrace();
        }
        return null;
    }

    public static String getResourceFileContent(Class clazz, String resourcePath) {
        StringWriter writer = new StringWriter();
        InputStream inputStream = clazz.getResourceAsStream(resourcePath);
        getStreamContent(writer, inputStream);
        return writer.toString();
    }

    public static void getStreamContent(final StringWriter writer, final InputStream inputStream) {
        try {
            IOUtils.copy(inputStream, writer, "UTF-8");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
