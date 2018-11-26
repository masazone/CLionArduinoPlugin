package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CMakeCommand extends CMakeCommandBase {
    public CMakeCommand(@NotNull final CMakeCommandType commandType, @NotNull final List<String> args, boolean isAddEOL, boolean commented, boolean suppressibleCommented) {
        super(commandType, args, isAddEOL, commented, suppressibleCommented);
    }

    public CMakeCommand(@NotNull final CMakeCommandType commandType, @NotNull final List<String> args, boolean isAddEOL) {
        this(commandType, args, isAddEOL, false, false);
    }

    public CMakeCommand(@NotNull final CMakeCommandType commandType, boolean isAddEOL) {
        this(commandType, new ArrayList<>(), isAddEOL, false, false);
    }

    public CMakeCommand(@NotNull final CMakeCommand other) {
        this(other.myCommandType, other.myArgs, other.myAddEOL, other.myCommented, other.mySuppressibleCommented);
    }

    @Override
    public String toString() {
        return "CMakeCommand{" +
                "" + myCommandType +
                ", =" + myArgs +
                '}';
    }
}
