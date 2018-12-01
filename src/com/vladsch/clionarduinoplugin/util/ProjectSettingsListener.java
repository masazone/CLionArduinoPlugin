package com.vladsch.clionarduinoplugin.util;

import com.intellij.util.messages.Topic;

public interface ProjectSettingsListener {
    Topic<ProjectSettingsListener> TOPIC = new Topic<>("Arduino Support Project Settings Changed", ProjectSettingsListener.class);

    void onSettingsChanged();
}
