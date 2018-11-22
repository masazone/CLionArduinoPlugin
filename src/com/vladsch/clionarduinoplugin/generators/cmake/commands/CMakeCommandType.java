package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CMakeCommandType {
    public static final String[] EMPTY = new String[0];
    public static final CMakeCommandType NULL = new CMakeCommandType("", "", EMPTY, 0,0);

    final private @NotNull String myName;
    final private @NotNull String myCommand;
    final private @NotNull String[] myFixedArgs;
    final private @NotNull String[] myDefaultArgs;
    final private boolean myNoDupeArgs;
    final private int myMinArgs;
    final private int myMaxArgs;
    final private boolean myIsMultiple;     // whether can have multiple commands
    final private boolean myIsKeepLast;   // if cannot have multiple then which one drives Last, First

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs) {
        this(name, command, fixedArgs, minArgs, maxArgs, false, true, true, null);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs) {
        this(name, command, fixedArgs, minArgs, maxArgs, noDupeArgs, true, true, null);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs, final boolean isMultiple, final boolean isKeepLast) {
        this(name, command, fixedArgs, minArgs, maxArgs, noDupeArgs, isMultiple, isKeepLast, null);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs, final boolean isMultiple, final boolean isKeepLast, @Nullable String[] defaults) {
        myName = name;
        myCommand = command;
        myFixedArgs = fixedArgs;
        myMinArgs = minArgs;
        myMaxArgs = maxArgs;
        myNoDupeArgs = noDupeArgs;
        myIsMultiple = isMultiple;
        myIsKeepLast = isKeepLast;
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

    public boolean isMultiple() {
        return myIsMultiple;
    }

    public boolean isKeepLast() {
        return myIsKeepLast;
    }

    @Override
    public String toString() {
        return "CMakeCommandType{" +
                "'" + myName + '\'' +
                ", '" + myCommand + '\'' +
                ", " + Arrays.toString(myFixedArgs) +
                ", [" + myMinArgs +
                ", " + myMaxArgs +
                "], NoDupes " + myNoDupeArgs +
                ", IsMulti " + myIsMultiple +
                '}';
    }
}
