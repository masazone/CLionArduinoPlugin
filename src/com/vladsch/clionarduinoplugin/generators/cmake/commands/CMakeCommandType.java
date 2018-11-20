package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CMakeCommandType {
    public static final String[] EMPTY = new String[0];

    final private @NotNull String myName;
    final private @NotNull String myCommand;
    final private @NotNull String[] myFixedArgs;
    final private @NotNull String[] myDefaultArgs;
    final private boolean myNoDupeArgs;

    // variable args
    final private int myMinArgs;
    final private int myMaxArgs;

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs) {
        this(name, command, fixedArgs, minArgs, maxArgs, false, null);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, boolean noDupeArgs) {
        this(name, command, fixedArgs, minArgs, maxArgs, noDupeArgs, null);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs, @Nullable String[] defaults) {
        myName = name;
        myCommand = command;
        myFixedArgs = fixedArgs;
        myMinArgs = minArgs;
        myMaxArgs = maxArgs;
        myNoDupeArgs = noDupeArgs;
        myDefaultArgs = defaults != null ? defaults : EMPTY;
    }

    @NotNull
    public String getName() {
        return myName;
    }

    @NotNull
    public String getCommand() {
        return myCommand;
    }

    @NotNull
    public String[] getFixedArgs() {
        return myFixedArgs;
    }

    public int getMinArgs() {
        return myMinArgs;
    }

    public int getMaxArgs() {
        return myMaxArgs;
    }

    public String[] getDefaultArgs() {
        return myDefaultArgs;
    }

    public boolean isNoDupeArgs() {
        return myNoDupeArgs;
    }

    @Override
    public String toString() {
        return "CMakeCommandType{" +
                "'" + myName + '\'' +
                ", '" + myCommand + '\'' +
                ", " + Arrays.toString(myFixedArgs) +
                '}';
    }
}
