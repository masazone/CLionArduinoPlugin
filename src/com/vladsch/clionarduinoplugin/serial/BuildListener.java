package com.vladsch.clionarduinoplugin.serial;

import org.jetbrains.annotations.NotNull;

public interface BuildListener {
    void beforeBuildStarted(@NotNull final String targetName);
    void afterBuildFinished(final boolean buildSuccess);
}
