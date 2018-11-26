package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CMakeUnknownCommand extends CMakeCommandBase {
    final protected @NotNull String myCommandName;

    public CMakeUnknownCommand(@NotNull final String commandName, @NotNull final List<String> args, boolean isAddEOL, boolean commented, boolean suppressibleCommented) {
        super(CMakeCommandType.UNKNOWN, args, isAddEOL, commented, suppressibleCommented);
        myCommandName = commandName;
    }

    public CMakeUnknownCommand(@NotNull final String commandName, @NotNull final List<String> args, boolean isAddEOL) {
        this(commandName, args, isAddEOL, false, false);
    }

    public CMakeUnknownCommand(@NotNull final String commandName, boolean isAddEOL) {
        this(commandName, new ArrayList<>(), isAddEOL, false, false);
    }

    public CMakeUnknownCommand(@NotNull final CMakeUnknownCommand other) {
        this(other.myCommandName, other.myArgs, other.myAddEOL, other.myCommented, other.mySuppressibleCommented);
    }

    @Override
    @NotNull
    public String getCommandName() {
        return myCommandName;
    }

    @Override
    public String toString() {
        return "CMakeUnknownCommand{" +
                "" + myCommandName +
                ", =" + myArgs +
                '}';
    }
}
