package com.vladsch.clionarduinoplugin.generators.cmake;

import com.intellij.openapi.diagnostic.Logger;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.Argument;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.Command;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CommentedOutCommand;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeElement;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeText;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

/**
 * Class for creating, reading and modifying CMakeLists.txt files with specific flavour
 * provided by specialized subclasses
 * <p>
 * This class knows the how and when of manipulating the file but no knowledge of what
 */
public abstract class CMakeListsTxtBuilder {
    // commands can have fixed and variable arguments
    // fixed arguments can have dependency on variable arguments of other commands in the command set for a given cmake file
    // this allows fixed args to be dependent on args of other commands or values in the command set
    //
    //
    private static final Logger LOG = getInstance("com.vladsch.clionarduinoplugin.generators");

    final static public int INF_MAX_ARGS = 1000;
    final static public Pattern COMMAND_REF = Pattern.compile("<\\$([a-zA-Z_$][a-zA-Z_0-9$]*)(?:\\[(\\d+)\\])?\\$>");

    final private @NotNull ArrayList<CMakeElement> myElements = new ArrayList<>();
    final private HashMap<CMakeElement, Node> myElementNodeMap = new HashMap<>();
    final private Map<String, CMakeCommandType> myCMakeCommands;
    final private Map<String, CMakeCommandType> myCommands;
    final private Map<String, CMakeCommandType> mySetCommands;
    final private Map<String, CMakeCommandType> mySetCommandsArg0;
    final private Map<CMakeCommandType, List<CMakeCommandAnchor>> myAnchorsMap;
    final private ArrayList<CMakeCommandType> myFirstAnchors;
    final private ArrayList<CMakeCommandType> myLastAnchors;
    final private HashMap<CMakeCommandType, ArrayList<CMakeCommandType>> myBeforeAnchorsMap;
    final private HashMap<CMakeCommandType, ArrayList<CMakeCommandType>> myAfterAnchorsMap;
    private @Nullable CMakeFile myCMakeFile = null;
    private boolean myWantCommentedOut;

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, final CMakeCommandAnchor[] anchors) {
        myCMakeCommands = new HashMap<>();
        myCommands = new HashMap<>();
        myAnchorsMap = new HashMap<>();
        mySetCommands = new HashMap<>();
        mySetCommandsArg0 = new HashMap<>();
        myWantCommentedOut = false;

        for (CMakeCommandType commandType : commands) {
            if ("set".equals(commandType.getCommand()) && commandType.getFixedArgs().length > 0) {
                mySetCommands.put(commandType.getName(), commandType);
                mySetCommandsArg0.put(commandType.getFixedArgs()[0], commandType);
            } else {
                myCommands.put(commandType.getName(), commandType);
                myCMakeCommands.put(commandType.getCommand(), commandType);
            }
        }

        myFirstAnchors = new ArrayList<>();
        myLastAnchors = new ArrayList<>();
        myBeforeAnchorsMap = new HashMap<>();
        myAfterAnchorsMap = new HashMap<>();

        for (CMakeCommandAnchor commandAnchor : anchors) {
            CMakeCommandType commandType = commandAnchor.getCommandType();
            CMakeCommandType commandAnchorType = commandAnchor.getCommandAnchor();
            ArrayList<CMakeCommandType> beforeList;
            ArrayList<CMakeCommandType> afterList;

            switch (commandAnchor.getAnchorType()) {
                case FIRST:
                    if (myLastAnchors.contains(commandType)) throw new IllegalStateException("CommandType " + commandType.getName() + " cannot be anchored first and last");
                    myFirstAnchors.add(commandType);
                    break;
                case LAST:
                    if (myFirstAnchors.contains(commandType)) throw new IllegalStateException("CommandType " + commandType.getName() + " cannot be anchored first and last");
                    myLastAnchors.add(commandType);
                    break;
                case BEFORE:
                    beforeList = myBeforeAnchorsMap.computeIfAbsent(commandAnchorType, type -> new ArrayList<>());
                    afterList = myBeforeAnchorsMap.get(commandAnchorType);
                    if (afterList != null && afterList.contains(commandType)) throw new IllegalStateException("CommandType " + commandType.getName() + " cannot be anchored before and after " + commandAnchorType.getName());
                    beforeList.add(commandType);
                    break;
                case AFTER:
                    afterList = myAfterAnchorsMap.computeIfAbsent(commandAnchorType, type -> new ArrayList<>());
                    beforeList = myBeforeAnchorsMap.get(commandAnchorType);
                    if (beforeList != null && beforeList.contains(commandType)) throw new IllegalStateException("CommandType " + commandType.getName() + " cannot be anchored before and after " + commandAnchorType.getName());
                    afterList.add(commandType);
                    break;
            }
        }

        for (CMakeCommandAnchor commandAnchor : anchors) {
            List<CMakeCommandAnchor> anchorList = myAnchorsMap.computeIfAbsent(commandAnchor.getCommandType(), commandType -> new ArrayList<>());
            anchorList.add(commandAnchor);
        }
    }

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, final CMakeCommandAnchor[] anchors, @NotNull CharSequence text, @Nullable DataHolder options) {
        this(commands, anchors, text, options, null);
    }

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, final CMakeCommandAnchor[] anchors, @NotNull CharSequence text, @Nullable DataHolder options, @Nullable Map<String, Object> values) {
        this(commands, anchors);

        HashMap<String, Object> valueSet = new HashMap<>();
        if (values != null) valueSet.putAll(values);

        CMakeParser parser = new CMakeParser(BasedSequenceImpl.of(text), options);
        myCMakeFile = parser.getDocument();

        // now load the commands
        for (Node node : myCMakeFile.getChildren()) {
            CMakeElement element = elementFrom(node, valueSet);
            addElement(element, node);

            if (element instanceof CMakeCommand) {
                String typeName = ((CMakeCommand) element).getCommandType().getName();
                valueSet.put(typeName, element);
            }
        }
    }

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, final CMakeCommandAnchor[] anchors, @NotNull CMakeFile cMakeFile) {
        this(commands, anchors, cMakeFile, null);
    }

    public CMakeListsTxtBuilder(CMakeCommandType[] commands, final CMakeCommandAnchor[] anchors, @NotNull CMakeFile cMakeFile, @Nullable Map<String, Object> values) {
        this(commands, anchors);

        HashMap<String, Object> valueSet = new HashMap<>();
        if (values != null) valueSet.putAll(values);

        myCMakeFile = cMakeFile;

        // now load the commands
        for (Node node : myCMakeFile.getChildren()) {
            CMakeElement element = elementFrom(node, valueSet);
            addElement(element, node);

            if (element instanceof CMakeCommand) {
                String typeName = ((CMakeCommand) element).getCommandType().getName();
                valueSet.put(typeName, element);
            }
        }
    }

    public @NotNull String getCMakeContents(@Nullable Map<String, Object> values) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, Object> valueSet = new HashMap<>();
        if (values != null) valueSet.putAll(values);

        for (CMakeElement element : myElements) {
            if (element instanceof CMakeCommand) {
                String typeName = ((CMakeCommand) element).getCommandType().getName();
                if (!valueSet.containsKey(typeName)) {
                    valueSet.put(typeName, element);
                }
            }
        }

        for (CMakeElement element : myElements) {
            try {
                element.appendTo(sb, valueSet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sb.length() > 0) sb.append("\n");
        return sb.toString();
    }

    public boolean isWantCommentedOut() {
        return myWantCommentedOut;
    }

    public void setWantCommentedOut(final boolean wantCommentedOut) {
        this.myWantCommentedOut = wantCommentedOut;
    }

    public CMakeElement elementFrom(@NotNull Node node, @Nullable Map<String, Object> valueSet) {
        // either command or text
        if (node instanceof Command) {
            // find the command or we can create a new one if we don't have it already or just make it into a text block
            Command command = (Command) node;
            CMakeCommandType commandType = null;
            ArrayList<String> commandArgs = new ArrayList<>();

            if (command.getCommand().equals("set")) {
                // see if we have a matching set command
                Argument arg = (Argument) command.getFirstChildAny(Argument.class);
                if (arg != null) {
                    String setCommand = arg.getText().toString();

                    commandType = mySetCommandsArg0.get(setCommand);
                    if (commandType != null) {
                        // TODO: check all fixed arguments not just the first one

                    } else {
                        // if the name contains a macro then it won't be expanded but the setCommand is expanded
                        // need to convert it by expanding dependent names
                        if (valueSet != null) {
                            for (String name : mySetCommandsArg0.keySet()) {
                                String converted = replacedCommandParams(name, valueSet);
                                if (converted.equals(setCommand)) {
                                    // it is this
                                    commandType = mySetCommandsArg0.get(name);
                                }
                            }
                        }
                    }
                }
            }

            if (commandType == null) {
                commandType = myCMakeCommands.get(command.getCommand().toString());
            }

            if (commandType != null) {
                CMakeCommand makeCommand = new CMakeCommand(commandType, false);
                int skipArgs = commandType.getFixedArgs().length;

                int i = 0;
                for (Node arg : node.getChildren()) {
                    if (arg instanceof Argument && i >= skipArgs) {
                        makeCommand.setArg(i - skipArgs, ((Argument) arg).getText().toString());
                    }
                    i++;
                }

                if (node instanceof CommentedOutCommand) {
                    makeCommand.commentOut(true);
                }
                return makeCommand;
            }
        }

        // create a text element
        return new CMakeText(node.getChars().toString(), false);
    }

    public @NotNull List<CMakeElement> getElements() {
        return myElements;
    }

    public void addElement(@NotNull CMakeElement element, Node node) {
        // no add eol adjustment, assumed to be done by caller
        myElementNodeMap.put(element, node);
        myElements.add(element);
    }

    public void addElement(@NotNull CMakeElement element) {
        myElements.add(element);
        fixAddEOL(myElements.size() - 1);
    }

    public void fixAddEOL(int insertedCommandIndex) {
        CMakeElement command = myElements.get(insertedCommandIndex);
        if (insertedCommandIndex == 0) {
            command.setAddEOL(true);
        } else if (insertedCommandIndex + 1 == myElements.size()) {
            CMakeElement prevElement = myElements.get(insertedCommandIndex - 1);
            command.setAddEOL(prevElement.isAddEOL());
            prevElement.setAddEOL(true);
        } else {
            CMakeElement prevElement = myElements.get(insertedCommandIndex - 1);
            if (prevElement instanceof CMakeCommand) {
                // steal prev command's addEOL, make it addEOL since this command now takes its place
                command.setAddEOL(prevElement.isAddEOL());
                prevElement.setAddEOL(true);
            }
        }
    }

    public void addElement(int index, @NotNull CMakeElement element) {
        myElements.add(index, element);
        fixAddEOL(index);
    }

    public void setElement(int index, @NotNull CMakeElement element) {
        if (index < myElements.size()) {
            element.setAddEOL(myElements.get(index).isAddEOL());
        } else {
            element.setAddEOL(true);
        }
        myElements.set(index, element);
    }

    public void removeElement(int index) {
        if (index + 1 < myElements.size()) myElements.get(index + 1).setAddEOL(myElements.get(index).isAddEOL());
        myElements.remove(index);
    }

    public void removeElement(@NotNull CMakeElement element) {
        int index = myElements.indexOf(element);
        if (index == -1) {
            throw new IllegalStateException("anchor element not found");
        }
        removeElement(index);
    }

    public int addElementBefore(@NotNull CMakeElement anchor, @NotNull CMakeElement element) {
        int index = myElements.indexOf(anchor);
        if (index == -1) {
            throw new IllegalStateException("anchor element not found");
        }

        addElement(index, element);
        return index;
    }

    public void addElementAfter(@NotNull CMakeElement anchor, @NotNull CMakeElement element) {
        int index = myElements.indexOf(anchor);

        if (index == -1) {
            throw new IllegalStateException("anchor element not found");
        }

        index++;
        addElement(index, element);
    }

    public void replaceElement(@NotNull CMakeElement anchor, @NotNull CMakeElement element) {
        int index = myElements.indexOf(anchor);
        if (index == -1) {
            throw new IllegalStateException("anchor element not found");
        }

        setElement(index, element);
    }

    public int getCommandIndex(String name) {
        CMakeCommandType commandType = getCommandType(name);
        int firstCommented = -1;

        if (commandType != null) {
            int i = 0;
            for (CMakeElement element : myElements) {
                if (element instanceof CMakeCommand) {
                    if (((CMakeCommand) element).getCommandType() == commandType) {
                        if (((CMakeCommand) element).isCommentedOut()) {
                            if (myWantCommentedOut && firstCommented == -1) {
                                firstCommented = i;
                            }
                        } else {
                            return i;
                        }
                    }
                }
                i++;
            }
        }
        return firstCommented;
    }

    public @Nullable CMakeCommand getCommand(String name) {
        int index = getCommandIndex(name);
        return index >= 0 ? (CMakeCommand) myElements.get(index) : null;
    }

    public CMakeCommandType getCommandType(final String name) {
        CMakeCommandType commandType = mySetCommands.get(name);
        if (commandType == null) {
            commandType = myCommands.get(name);
        }
        return commandType;
    }

    private boolean afterAnchorIndex(@NotNull IndexRange range, @Nullable CMakeCommandType commandType) {
        if (commandType != null) {
            int anchorIndex = getCommandIndex(commandType.getName());
            if (anchorIndex >= 0 && anchorIndex >= range.afterIndex) {
                range.afterIndex = anchorIndex + 1;
                return true;
            }
        }
        return false;
    }

    private void beforeAnchorIndex(@NotNull IndexRange range, @Nullable CMakeCommandType commandType) {
        if (commandType != null) {
            int anchorIndex = getCommandIndex(commandType.getName());
            if (anchorIndex >= 0 && anchorIndex < range.beforeIndex) range.beforeIndex = anchorIndex;
        }
    }

    private static class IndexRange {
        final int originalBefore;
        final int originalAfter;

        int beforeIndex;
        int afterIndex;

        public IndexRange(final int beforeIndex, final int afterIndex) {
            originalBefore = beforeIndex;
            originalAfter = afterIndex;

            this.beforeIndex = beforeIndex;
            this.afterIndex = afterIndex;
        }

        public boolean isUnmodified() {
            return originalAfter == afterIndex && originalBefore == beforeIndex;
        }

        public boolean isUnmodifiedBefore() {
            return originalBefore == beforeIndex;
        }

        public boolean isUnmodifiedAfter() {
            return originalAfter == afterIndex;
        }

        @Override
        public String toString() {
            return "IndexRange [" +
                    "" + afterIndex +
                    " - " + beforeIndex +
                    ']';
        }
    }

    private void adjustIndexRange(@NotNull CMakeCommand command, @NotNull IndexRange range, @NotNull Collection<CMakeCommandType> siblings, @NotNull AnchorType siblingsAnchorType, boolean isMember) {
        boolean isAfter;
        boolean isOnSelfAfter;
        boolean hadSelf = false;

        switch (siblingsAnchorType) {
            case LAST:
                if (isMember) {
                    isAfter = true;
                    isOnSelfAfter = false;
                } else {
                    isAfter = false;
                    isOnSelfAfter = false;
                }
                break;

            case FIRST:
                if (isMember) {
                    isAfter = true;
                    isOnSelfAfter = false;
                } else {
                    isAfter = true;
                    isOnSelfAfter = true;
                }
                break;

            default:
            case AFTER:
            case BEFORE:
                isAfter = true;
                isOnSelfAfter = false;
                break;
        }

        for (CMakeCommandType siblingType : siblings) {
            if (siblingType == command.getCommandType()) {
                isAfter = isOnSelfAfter;
                hadSelf = true;
            } else {
                if (isAfter) {
                    afterAnchorIndex(range, siblingType);
                } else {
                    beforeAnchorIndex(range, siblingType);
                }
            }
        }

        switch (siblingsAnchorType) {
            case FIRST:
                if (hadSelf) {
                    if (range.isUnmodified()) {
                        range.beforeIndex = 0;
                    }
                }
                break;

            case LAST:
                if (hadSelf) {
                    if (range.isUnmodified()) {
                        range.afterIndex = myElements.size();
                    }
                }
                break;
        }
    }

    /**
     * Place according to anchors or at the end if no anchors for this command
     * <p>
     * Heuristic, Not efficient but does the job
     *
     * @param command command to place in the file
     */
    public void addCommand(@NotNull CMakeCommand command) {
        List<CMakeCommandAnchor> anchors = myAnchorsMap.get(command.getCommandType());
        // go through the list and try to satisfy conditions
        IndexRange range = new IndexRange(myElements.size(), 0);
        boolean afterHasPriority = false;

        if (anchors == null) {
            // make respect firsts, lasts, and any of its own dependents
            adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false);
            adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false);
            ArrayList<CMakeCommandType> beforeDependents = myBeforeAnchorsMap.get(command.getCommandType());
            ArrayList<CMakeCommandType> afterDependents = myAfterAnchorsMap.get(command.getCommandType());
            if (beforeDependents != null) {
                // treat them as first (ie. before this node)
                adjustIndexRange(command, range, beforeDependents, AnchorType.FIRST, false);
            }
            if (afterDependents != null) {
                // treat them as last (ie. after this node)
                adjustIndexRange(command, range, afterDependents, AnchorType.LAST, false);
            }
        } else {
            for (CMakeCommandAnchor anchor : anchors) {
                switch (anchor.getAnchorType()) {
                    case FIRST:
                        adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, true);
                        //adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false);
                        break;

                    case BEFORE:
                        adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false);
                        adjustIndexRange(command, range, myBeforeAnchorsMap.get(anchor.getCommandAnchor()), AnchorType.BEFORE, true);

                        beforeAnchorIndex(range, anchor.getCommandAnchor());

                        // treat any of the after of the same parent as last so this command will come before
                        adjustIndexRange(command, range, myAfterAnchorsMap.get(anchor.getCommandAnchor()), AnchorType.LAST, false);
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false);
                        break;

                    case AFTER:
                        adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false);
                        // treat any of the before of the same parent as firsts so this command will come after
                        adjustIndexRange(command, range, myBeforeAnchorsMap.get(anchor.getCommandAnchor()), AnchorType.FIRST, false);

                        if (afterAnchorIndex(range, anchor.getCommandAnchor())) {
                            // found true anchor, use the afterIndex for placement, the before may be too low in the file
                            afterHasPriority = true;
                        }

                        adjustIndexRange(command, range, myAfterAnchorsMap.get(anchor.getCommandAnchor()), AnchorType.AFTER, true);
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false);
                        break;

                    case LAST:
                        //adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false);
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, true);
                        break;
                }
            }
        }
        if (range.beforeIndex == myElements.size() && range.afterIndex == 0) {
            // no anchors, goes last
            addElement(command);
        } else if (range.beforeIndex == myElements.size()) {
            // no before anchor, goes after
            addElement(range.afterIndex, command);
        } else if (range.afterIndex == 0) {
            // no after anchor, goes before
            addElement(range.beforeIndex, command);
        } else {
            if (range.beforeIndex < range.afterIndex) throw new IllegalStateException("Invalid anchor definitions: BeforeAnchor(" + range.beforeIndex + ") < AfterAnchor(" + range.afterIndex + ") for " + command.getCommandType().getName());
            // insert before
            addElement(afterHasPriority ? range.afterIndex : range.beforeIndex, command);
        }
    }

    @Nullable
    CMakeCommand setCommand(@NotNull String name, String... args) {
        return setCommand(name, Arrays.asList(args));
    }

    @NotNull
    CMakeCommand setOrAddCommand(@NotNull String name, String... args) {
        return setOrAddCommand(name, Arrays.asList(args));
    }

    @SuppressWarnings("SameParameterValue")
    void removeCommand(@NotNull String name) {
        int index;
        do {
            index = getCommandIndex(name);
            if (index != -1) {
                removeElement(index);
            }
        } while (index != -1);
    }

    /**
     * Replace, modify or insert command based on command type and command anchors information
     *
     * @param name command name (not CMake command name but builder specific name)
     * @param args arguments for the command
     * @return resulting command (new or modified)
     */
    CMakeCommand setOrAddCommand(@NotNull String name, @Nullable Collection<String> args) {
        CMakeCommand command = setCommand(name, args);
        CMakeCommand newCommand = command;
        if (command == null) {
            // create a new one and find where to insert it
            CMakeCommandType commandType = getCommandType(name);
            if (commandType == null) {
                throw new IllegalArgumentException("Unknown command name " + name);
            }
            //noinspection unchecked
            List<String> argList = args == null ? Collections.EMPTY_LIST : new ArrayList<>(args);
            newCommand = new CMakeCommand(commandType, argList);
            addCommand(newCommand);
        }
        return newCommand;
    }

    /**
     * Replace or modify a command based on command type and command anchors information
     * if the command does not exist in the builder then nothing is done and null is returned.
     *
     * @param name command name (not CMake command name but builder specific name)
     * @param args arguments for the command
     * @return resulting command (new or modified)
     */
    @Nullable
    CMakeCommand setCommand(@NotNull String name, @Nullable Collection<String> args) {
        CMakeCommand command = getCommand(name);
        CMakeCommand newCommand = command;

        if (command != null) {
            //noinspection unchecked
            List<String> argList = args == null ? Collections.EMPTY_LIST : new ArrayList<>(args);
            CMakeCommandType commandType = command.getCommandType();

            if (!commandType.isMultiple() && commandType.getMaxArgs() == CMakeListsTxtBuilder.INF_MAX_ARGS) {
                if (myElementNodeMap.get(command) != null) {
                    // original command, replace it with new
                    newCommand = new CMakeCommand(command);
                    newCommand.commentOut(false);
                    newCommand.addAll(argList);
                    replaceElement(command, newCommand);
                } else {
                    newCommand.addAll(argList);
                }
            } else {
                if (commandType.isMultiple()) {
                    if (commandType.isNoDupeArgs()) {
                        // add another one after this one if the arg value is different
                        if (!command.allArgsEqual(argList)) {
                            newCommand = new CMakeCommand(command.getCommandType(), argList);
                            newCommand.commentOut(false);
                            addElementAfter(command, newCommand);
                        }
                    } else {
                        // add another one after this one
                        newCommand = new CMakeCommand(command.getCommandType(), argList);
                        addElementAfter(command, newCommand);
                    }
                } else {
                    if (myElementNodeMap.get(command) != null) {
                        // original command, replace it with new
                        newCommand = new CMakeCommand(command);
                        newCommand.commentOut(false);
                        newCommand.setArgs(argList);
                        replaceElement(command, newCommand);
                    } else {
                        newCommand.setArgs(argList);
                    }
                }
            }
        }
        return newCommand;
    }

    /**
     * replace other commands' argument references in the given string
     *
     * <p>
     * <$COMMAND_NAME$> refers to variable arg 0 of command with name COMMAND_NAME or string mapped by COMMAND_NAME
     * <$COMMAND_NAME[2]$> refers to variable arg 2 of command with name COMMAND_NAME
     * <$COMMAND_NAME[]$> invalid, always empty result
     * <$COMMAND_NAME[-1]$> first from from the end, ie. command.getArgCount() - 1
     * <$COMMAND_NAME[-3]$> third from from the end, ie. command.getArgCount() - 3
     * <p>
     * NOTE: if Value or Command is not found or index is invalid then it is the same as the value not being empty
     * <p>
     * if command is not found or has less args then an empty value will be used
     *
     * @param arg      string with possible command references and variable value references
     * @param valueSet map of names to values, if value is CMakeCommand then its argument value will be extracted, otherwise the argument value will be the String value of passed object
     * @return string with variables replaced
     */
    static public String replacedCommandParams(@NotNull String arg, Map<String, Object> valueSet) {
        Matcher matcher = COMMAND_REF.matcher(arg);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            do {
                String commandRef = matcher.group(1);
                Object ref = valueSet.get(commandRef);
                String value = "";

                if (ref != null) {
                    if (ref instanceof CMakeCommand) {
                        CMakeCommand command = (CMakeCommand) ref;
                        int index = 0;
                        value = matcher.group(2);
                        if (value != null) {
                            try {
                                index = Integer.parseInt(value);
                            } catch (NumberFormatException ignored) {
                                index = command.getArgCount();
                            }
                        }

                        if (index < 0) {
                            index += command.getArgCount();
                        }

                        value = index >= 0 && index < command.getArgCount() ? command.getArg(index) : "";
                    } else {
                        value = ref.toString();
                    }
                }

                matcher.appendReplacement(sb, value);
            } while (matcher.find());

            matcher.appendTail(sb);
            return sb.toString();
        }
        return arg;
    }
}
