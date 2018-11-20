package com.vladsch.clionarduinoplugin.util;

import com.intellij.util.messages.Topic;

public interface ApplicationSettingsListener {
    final static public Topic<ApplicationSettingsListener> TOPIC = new Topic<>("Arduino Support Application Settings Changed", ApplicationSettingsListener.class);

    void onSettingsChanged();
}
