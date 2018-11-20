package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public interface CMakeElement {
    @NotNull String getText(@Nullable Map<String, Object> valueSet);
    void appendTo(StringBuilder out, @Nullable Map<String, Object> valueSet) throws IOException;

    void setAddEOL(boolean addEOL);
    boolean isAddEOL();
}
