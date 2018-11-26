package com.vladsch.clionarduinoplugin.util;

import com.intellij.util.messages.Topic;
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings;

public interface ApplicationSettingsListener {
    final static public Topic<ApplicationSettingsListener> TOPIC = new Topic<>("Arduino Support Application Settings Changed", ApplicationSettingsListener.class);

    void onSettingsChanged(ArduinoApplicationSettings settings);
}
