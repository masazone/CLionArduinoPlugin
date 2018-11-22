package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMakeCommandAnchor {
    final private @NotNull AnchorType myAnchorType;
    final private @NotNull CMakeCommandType myCommandType;
    final private @Nullable CMakeCommandType myCommandAnchor;

    private CMakeCommandAnchor(@NotNull final AnchorType anchorType, @NotNull final CMakeCommandType commandType, @Nullable CMakeCommandType commandAnchor) {
        myAnchorType = anchorType;
        myCommandType = commandType;
        myCommandAnchor = commandAnchor;
    }

    public AnchorType getAnchorType() {
        return myAnchorType;
    }

    public CMakeCommandType getCommandType() {
        return myCommandType;
    }

    public CMakeCommandType getCommandAnchor() {
        return myCommandAnchor;
    }

    @Override
    public String toString() {
        return "CMakeCommandAnchor{" +
                "" + myCommandType.getName() +
                " " + myAnchorType.name() +
                (myCommandAnchor == null ? "" : " " + myCommandAnchor.getName()) +
                '}';
    }

    static public CMakeCommandAnchor first(@NotNull CMakeCommandType commandType) {
        return new CMakeCommandAnchor(AnchorType.FIRST, commandType, null);
    }

    static public CMakeCommandAnchor last(@NotNull CMakeCommandType commandType) {
        return new CMakeCommandAnchor(AnchorType.LAST, commandType, null);
    }

    static public CMakeCommandAnchor before(@NotNull CMakeCommandType commandAnchor, @NotNull CMakeCommandType commandType) {
        return new CMakeCommandAnchor(AnchorType.BEFORE, commandType, commandAnchor);
    }

    static public CMakeCommandAnchor after(@NotNull CMakeCommandType commandAnchor, @NotNull CMakeCommandType commandType) {
        return new CMakeCommandAnchor(AnchorType.AFTER, commandType, commandAnchor);
    }
}
