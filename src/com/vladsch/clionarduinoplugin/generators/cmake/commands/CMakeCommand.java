package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import com.vladsch.clionarduinoplugin.generators.cmake.CMakeListsTxtBuilder;
import com.vladsch.clionarduinoplugin.generators.cmake.CMakeParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class CMakeCommand implements CMakeElement {
    final protected @NotNull CMakeCommandType myCommandType;
    final protected @NotNull ArrayList<String> myArgs;
    protected boolean myAddEOL;
    protected boolean myCommented;
    protected boolean mySuppressibleCommented;

    public CMakeCommand(@NotNull final CMakeCommandType commandType, @NotNull final List<String> args, boolean isAddEOL, boolean commented, boolean suppressibleCommented) {
        myCommandType = commandType;
        myArgs = new ArrayList<>(args);
        myAddEOL = isAddEOL;
        myCommented = commented;
        mySuppressibleCommented = suppressibleCommented;

        String[] defaults = commandType.getDefaultArgs();
        int iMax = defaults.length;

        // set defaults for missing or null values
        for (int i = 0; i < iMax; i++) {
            if (i >= myArgs.size() || myArgs.get(i) == null) {
                addArg(defaults[i]);
            }
        }
    }

    public CMakeCommand(@NotNull final CMakeCommandType commandType, @NotNull final List<String> args) {
        this(commandType, args, true, false, false);
    }

    public CMakeCommand(@NotNull final CMakeCommandType commandType, boolean isAddEOL) {
        this(commandType, new ArrayList<>(), isAddEOL,false,false);
    }

    public CMakeCommand(@NotNull final CMakeCommandType commandType) {
        this(commandType, new ArrayList<>(), true,false,false);
    }

    public CMakeCommand(@NotNull final CMakeCommand other) {
        this(other.myCommandType, other.myArgs, other.myAddEOL, other.myCommented, other.mySuppressibleCommented);
    }

    public boolean isSuppressibleCommented() {
        return mySuppressibleCommented;
    }

    public void setSuppressibleCommented(final boolean suppressibleCommented) {
        mySuppressibleCommented = suppressibleCommented;
    }

    public void setSuppressible(final boolean suppressibleCommented) {
        if (suppressibleCommented) myCommented = true;
        mySuppressibleCommented = suppressibleCommented;
    }

    public boolean isCommented() {
        return myCommented;
    }

    public void commentOut(final boolean commentedOut) {
        myCommented = commentedOut;
    }

    public void setCommentOut(final boolean commentedOut) {
        myCommented = commentedOut;
    }

    @Override
    public String getText(@Nullable Map<String, Object> valueSet, final boolean suppressCommented) {
        StringBuilder sb = new StringBuilder();
        try {
            appendTo(sb, valueSet, suppressCommented);
        } catch (IOException ignored) {

        }
        return sb.toString();
    }

    @Override
    public boolean isAddEOL() {
        return myAddEOL;
    }

    @Override
    public void setAddEOL(final boolean addEOL) {
        myAddEOL = addEOL;
    }

    @Override
    public void appendTo(StringBuilder out, @Nullable Map<String, Object> valueSet, final boolean suppressCommented) throws IOException {
        if (myCommented) {
            if (mySuppressibleCommented && suppressCommented) return;
            out.append("# ");
        }

        out.append(myCommandType.getCommand());
        out.append("(");
        String sep = "";

        HashSet<String> argValues = new HashSet<>();

        for (String arg : myCommandType.getFixedArgs()) {
            if (arg.isEmpty()) continue;

            if (!myCommandType.isNoDupeArgs() || !argValues.contains(arg)) {
                argValues.add(arg);

                out.append(sep);
                sep = " ";

                out.append(CMakeParser.getArgText(CMakeListsTxtBuilder.Companion.replacedCommandParams(arg, valueSet)));
            }
        }

        for (String arg : myArgs) {
            if (arg.isEmpty()) continue;

            if (!myCommandType.isNoDupeArgs() || !argValues.contains(arg)) {
                argValues.add(arg);

                out.append(sep);
                sep = " ";
                out.append(CMakeParser.getArgText(arg));
            }
        }

        if (myAddEOL) {
            out.append(")\n");
        } else {
            out.append(")");
        }
    }

    @NotNull
    public CMakeCommandType getCommandType() {
        return myCommandType;
    }

    public List<String> getArgs() {
        return myArgs;
    }

    public int getArgCount() {
        return myArgs.size();
    }

    public void setArgs(@NotNull Collection<String> args) {
        myArgs.clear();
        myArgs.addAll(args);
    }

    public void setArg(int index, @NotNull String arg) {
        if (index == getArgCount()) {
            myArgs.add(arg);
        } else {
            myArgs.set(index, arg);
        }
    }

    public @NotNull String getArg(int index) {
        return myArgs.get(index);
    }

    public void addArg(@NotNull String arg) {
        myArgs.add(arg);
    }

    public void extendArgs(int index) {
        while (index >= myArgs.size()) myArgs.add("");
    }

    public void setAll(@NotNull Collection<String> args) {
        int i = 0;
        for (String arg : args) {
            if (arg != null) {
                if (i < myArgs.size()) {
                    extendArgs(i);
                    myArgs.set(i, arg);
                } else {
                    myArgs.add(arg);
                }
            }
            i++;
        }
    }

    public void addAll(@NotNull Collection<String> args) {
        for (String arg : args) {
            if (arg != null) {
                myArgs.add(arg);
            }
        }
    }

    public boolean allArgsEqual(final Collection<String> args) {
        return allArgsEqual(args,0, args.size());
    }

    public boolean allArgsEqual(final Collection<String> args, int start, int end) {
        int i = 0;
        for (String arg : args) {
            if (i >= end) return true;

            if (i >= start) {
                if (arg != null) {
                    if (i < myArgs.size()) {
                        if (!arg.equals(myArgs.get(i))) {
                            return false;
                        }
                    } else {
                        if (!arg.isEmpty()) {
                            return false;
                        }
                    }
                } else {
                    if (!myArgs.get(i).isEmpty()) {
                        return false;
                    }
                }
            }
            i++;
        }
        return true;
    }

    public void clearArgs() {
        myArgs.clear();
    }

    public void addArg(int index, @NotNull String arg) {
        myArgs.set(index, arg);
    }

    public void removeArg(int index) {
        myArgs.remove(index);
    }

    @Override
    public String toString() {
        return "CMakeCommand{" +
                "" + myCommandType +
                ", =" + myArgs +
                '}';
    }
}
