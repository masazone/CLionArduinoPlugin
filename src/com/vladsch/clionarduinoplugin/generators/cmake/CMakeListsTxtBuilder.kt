package com.vladsch.clionarduinoplugin.generators.cmake

import com.intellij.openapi.diagnostic.Logger.getInstance
import com.vladsch.clionarduinoplugin.generators.cmake.ast.Argument
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile
import com.vladsch.clionarduinoplugin.generators.cmake.ast.Command
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CommentedOutCommand
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeElement
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeText
import com.vladsch.flexmark.ast.Node
import com.vladsch.flexmark.util.options.DataHolder
import com.vladsch.flexmark.util.sequence.BasedSequenceImpl
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

/**
 * Class for creating, reading and modifying CMakeLists.txt files with specific flavour
 * provided by specialized subclasses
 *
 *
 * This class knows the how and when of manipulating the file but no knowledge of what
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class CMakeListsTxtBuilder(commands: Array<CMakeCommandType>, anchors: Array<CMakeCommandAnchor>) {

    private val myElements = ArrayList<CMakeElement>()
    private val myElementNodeMap = HashMap<CMakeElement, Node>()
    private val myCMakeCommands: MutableMap<String, CMakeCommandType>
    private val myCommands: MutableMap<String, CMakeCommandType>
    private val mySetCommands: MutableMap<String, CMakeCommandType>
    private val mySetCommandsArg0: MutableMap<String, CMakeCommandType>
    private val myAnchorsMap: MutableMap<CMakeCommandType, ArrayList<CMakeCommandAnchor>>
    private val myFirstAnchors: ArrayList<CMakeCommandType>
    private val myLastAnchors: ArrayList<CMakeCommandType>
    private val myBeforeAnchorsMap: HashMap<CMakeCommandType, ArrayList<CMakeCommandType>>
    private val myAfterAnchorsMap: HashMap<CMakeCommandType, ArrayList<CMakeCommandType>>
    private var myCMakeFile: CMakeFile? = null
    var isWantCommentedOut: Boolean = false

    val elements: List<CMakeElement>
        get() = myElements

    init {
        myCMakeCommands = HashMap()
        myCommands = HashMap()
        myAnchorsMap = HashMap()
        mySetCommands = HashMap()
        mySetCommandsArg0 = HashMap()
        isWantCommentedOut = false

        for (commandType in commands) {
            if ("set" == commandType.command && commandType.fixedArgs.size > 0) {
                mySetCommands[commandType.name] = commandType
                mySetCommandsArg0[commandType.fixedArgs[0]] = commandType
            } else {
                myCommands[commandType.name] = commandType
                myCMakeCommands[commandType.command] = commandType
            }
        }

        myFirstAnchors = ArrayList()
        myLastAnchors = ArrayList()
        myBeforeAnchorsMap = HashMap()
        myAfterAnchorsMap = HashMap()

        for (commandAnchor in anchors) {
            val commandType = commandAnchor.commandType
            val commandAnchorType = commandAnchor.commandAnchor
            val beforeList: ArrayList<CMakeCommandType>?
            val afterList: ArrayList<CMakeCommandType>?

            when (commandAnchor.anchorType) {
                AnchorType.FIRST -> {
                    if (myLastAnchors.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored first and last")
                    myFirstAnchors.add(commandType)
                }
                AnchorType.LAST -> {
                    if (myFirstAnchors.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored first and last")
                    myLastAnchors.add(commandType)
                }
                AnchorType.BEFORE -> {
                    beforeList = myBeforeAnchorsMap.computeIfAbsent(commandAnchorType) { type -> ArrayList() }
                    afterList = myBeforeAnchorsMap[commandAnchorType]
                    if (afterList != null && afterList.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored before and after " + commandAnchorType.name)
                    beforeList.add(commandType)
                }
                AnchorType.AFTER -> {
                    afterList = myAfterAnchorsMap.computeIfAbsent(commandAnchorType) { type -> ArrayList() }
                    beforeList = myBeforeAnchorsMap[commandAnchorType]
                    if (beforeList != null && beforeList.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored before and after " + commandAnchorType.name)
                    afterList.add(commandType)
                }
            }
        }

        for (commandAnchor in anchors) {
            val anchorList = myAnchorsMap.computeIfAbsent(commandAnchor.commandType) { commandType -> ArrayList() }
            anchorList.add(commandAnchor)
        }
    }

    @JvmOverloads
    constructor(commands: Array<CMakeCommandType>, anchors: Array<CMakeCommandAnchor>, text: CharSequence, options: DataHolder?, values: Map<String, Any>? = null) : this(commands, anchors) {

        val valueSet = HashMap<String, Any>()
        if (values != null) valueSet.putAll(values)

        val parser = CMakeParser(BasedSequenceImpl.of(text), options)
        myCMakeFile = parser.document

        // now load the commands
        for (node in myCMakeFile!!.children) {
            val element = elementFrom(node, valueSet)
            addElement(element, node)

            if (element is CMakeCommand) {
                val typeName = element.commandType.name
                valueSet[typeName] = element
            }
        }
    }

    @JvmOverloads
    constructor(commands: Array<CMakeCommandType>, anchors: Array<CMakeCommandAnchor>, cMakeFile: CMakeFile, values: Map<String, Any>? = null) : this(commands, anchors) {

        val valueSet = HashMap<String, Any>()
        if (values != null) valueSet.putAll(values)

        myCMakeFile = cMakeFile

        // now load the commands
        for (node in myCMakeFile!!.children) {
            val element = elementFrom(node, valueSet)
            addElement(element, node)

            if (element is CMakeCommand) {
                val typeName = element.commandType.name
                valueSet[typeName] = element
            }
        }
    }

    fun getCMakeContents(values: Map<String, Any>?): String {
        val sb = StringBuilder()
        val valueSet = HashMap<String, Any>()
        if (values != null) valueSet.putAll(values)

        for (element in myElements) {
            if (element is CMakeCommand) {
                val typeName = element.commandType.name
                if (!valueSet.containsKey(typeName)) {
                    valueSet[typeName] = element
                }
            }
        }

        for (element in myElements) {
            try {
                element.appendTo(sb, valueSet)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (sb.isNotEmpty()) sb.append("\n")
        return sb.toString()
    }

    fun elementFrom(node: Node, valueSet: Map<String, Any>?): CMakeElement {
        // either command or text
        if (node is Command) {
            // find the command or we can create a new one if we don't have it already or just make it into a text block
            val command = node
            var commandType: CMakeCommandType? = null

            if (command.command.equals("set")) {
                // see if we have a matching set command
                val arg = command.getFirstChildAny(Argument::class.java) as? Argument
                if (arg != null) {
                    val setCommand = arg.text.toString()

                    commandType = mySetCommandsArg0[setCommand]
                    if (commandType != null) {
                        // TODO: check all fixed arguments not just the first one
                    } else {
                        // if the name contains a macro then it won't be expanded but the setCommand is expanded
                        // need to convert it by expanding dependent names
                        if (valueSet != null) {
                            for (name in mySetCommandsArg0.keys) {
                                val converted = replacedCommandParams(name, valueSet)
                                if (converted == setCommand) {
                                    // it is this
                                    commandType = mySetCommandsArg0[name]
                                }
                            }
                        }
                    }
                }
            }

            if (commandType == null) {
                commandType = myCMakeCommands[command.command.toString()]
            }

            if (commandType != null) {
                val makeCommand = CMakeCommand(commandType, false)
                val skipArgs = commandType.fixedArgs.size

                for ((i, arg) in node.getChildren().withIndex()) {
                    if (arg is Argument && i >= skipArgs) {
                        makeCommand.setArg(i - skipArgs, arg.text.toString())
                    }
                }

                if (node is CommentedOutCommand) {
                    makeCommand.commentOut(true)
                }
                return makeCommand
            }
        }

        // create a text element
        return CMakeText(node.chars.toString(), false)
    }

    fun addElement(element: CMakeElement, node: Node) {
        // no add eol adjustment, assumed to be done by caller
        myElementNodeMap[element] = node
        myElements.add(element)
    }

    fun addElement(element: CMakeElement) {
        myElements.add(element)
        fixAddEOL(myElements.size - 1)
    }

    fun fixAddEOL(insertedCommandIndex: Int) {
        val command = myElements[insertedCommandIndex]
        if (insertedCommandIndex == 0) {
            command.isAddEOL = true
        } else if (insertedCommandIndex + 1 == myElements.size) {
            val prevElement = myElements[insertedCommandIndex - 1]
            command.isAddEOL = prevElement.isAddEOL
            prevElement.isAddEOL = true
        } else {
            val prevElement = myElements[insertedCommandIndex - 1]
            if (prevElement is CMakeCommand) {
                // steal prev command's addEOL, make it addEOL since this command now takes its place
                command.isAddEOL = prevElement.isAddEOL()
                prevElement.setAddEOL(true)
            }
        }
    }

    fun addElement(index: Int, element: CMakeElement) {
        myElements.add(index, element)
        fixAddEOL(index)
    }

    fun setElement(index: Int, element: CMakeElement) {
        if (index < myElements.size) {
            element.isAddEOL = myElements[index].isAddEOL
        } else {
            element.isAddEOL = true
        }
        myElements[index] = element
    }

    fun removeElement(index: Int) {
        if (index + 1 < myElements.size) myElements[index + 1].isAddEOL = myElements[index].isAddEOL
        myElements.removeAt(index)
    }

    fun removeElement(element: CMakeElement) {
        val index = myElements.indexOf(element)
        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }
        removeElement(index)
    }

    fun addElementBefore(anchor: CMakeElement, element: CMakeElement): Int {
        val index = myElements.indexOf(anchor)
        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }

        addElement(index, element)
        return index
    }

    fun addElementAfter(anchor: CMakeElement, element: CMakeElement) {
        var index = myElements.indexOf(anchor)

        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }

        index++
        addElement(index, element)
    }

    fun replaceElement(anchor: CMakeElement, element: CMakeElement) {
        val index = myElements.indexOf(anchor)
        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }

        setElement(index, element)
    }

    fun getCommandIndex(name: String): Int {
        val commandType = getCommandType(name)
        return getCommandIndex(commandType)
    }

    fun getCommandIndex(commandType: CMakeCommandType?): Int {
        var firstCommented = -1

        if (commandType != null) {
            for ((i, element) in myElements.withIndex()) {
                if (element is CMakeCommand) {
                    if (element.commandType === commandType) {
                        if (element.isCommentedOut) {
                            if (isWantCommentedOut && firstCommented == -1) {
                                firstCommented = i
                            }
                        } else {
                            return i
                        }
                    }
                }
            }
        }
        return firstCommented
    }

    fun getCommand(name: String): CMakeCommand? {
        val index = getCommandIndex(name)
        return if (index >= 0) myElements[index] as CMakeCommand else null
    }

    fun getCommand(commandType: CMakeCommandType?): CMakeCommand? {
        val index = getCommandIndex(commandType)
        return if (index >= 0) myElements[index] as CMakeCommand else null
    }

    fun getCommandType(name: String): CMakeCommandType? {
        var commandType: CMakeCommandType? = mySetCommands[name]
        if (commandType == null) {
            commandType = myCommands[name]
        }
        return commandType
    }

    private fun afterAnchorIndex(range: IndexRange, commandType: CMakeCommandType?): Boolean {
        if (commandType != null) {
            val anchorIndex = getCommandIndex(commandType.name)
            if (anchorIndex >= 0 && anchorIndex >= range.afterIndex) {
                range.afterIndex = anchorIndex + 1
                return true
            }
        }
        return false
    }

    private fun beforeAnchorIndex(range: IndexRange, commandType: CMakeCommandType?) {
        if (commandType != null) {
            val anchorIndex = getCommandIndex(commandType.name)
            if (anchorIndex >= 0 && anchorIndex < range.beforeIndex) range.beforeIndex = anchorIndex
        }
    }

    private class IndexRange(val originalBefore: Int, val originalAfter: Int) {
        var beforeIndex: Int = 0
        var afterIndex: Int = 0

        val isUnmodified: Boolean
            get() = originalAfter == afterIndex && originalBefore == beforeIndex

        val isUnmodifiedBefore: Boolean
            get() = originalBefore == beforeIndex

        val isUnmodifiedAfter: Boolean
            get() = originalAfter == afterIndex

        init {

            this.beforeIndex = originalBefore
            this.afterIndex = originalAfter
        }

        override fun toString(): String {
            return "IndexRange [" +
                    "" + afterIndex +
                    " - " + beforeIndex +
                    ']'.toString()
        }
    }

    private fun adjustIndexRange(command: CMakeCommand, range: IndexRange, siblings: Collection<CMakeCommandType>?, siblingsAnchorType: AnchorType, isMember: Boolean) {
        if (siblings == null) return

        var isAfter: Boolean
        val isOnSelfAfter: Boolean
        var hadSelf = false

        when (siblingsAnchorType) {
            AnchorType.LAST -> if (isMember) {
                isAfter = true
                isOnSelfAfter = false
            } else {
                isAfter = false
                isOnSelfAfter = false
            }

            AnchorType.FIRST -> if (isMember) {
                isAfter = true
                isOnSelfAfter = false
            } else {
                isAfter = true
                isOnSelfAfter = true
            }

            AnchorType.AFTER, AnchorType.BEFORE -> {
                isAfter = true
                isOnSelfAfter = false
            }
        }

        for (siblingType in siblings) {
            if (siblingType === command.commandType) {
                isAfter = isOnSelfAfter
                hadSelf = true
            } else {
                if (isAfter) {
                    afterAnchorIndex(range, siblingType)
                } else {
                    beforeAnchorIndex(range, siblingType)
                }
            }
        }

        when (siblingsAnchorType) {
            AnchorType.FIRST -> if (hadSelf) {
                if (range.isUnmodified) {
                    range.beforeIndex = 0
                }
            }

            AnchorType.LAST -> if (hadSelf) {
                if (range.isUnmodified) {
                    range.afterIndex = myElements.size
                }
            }
            else -> {

            }
        }
    }

    /**
     * Place according to anchors or at the end if no anchors for this command
     *
     *
     * Heuristic, Not efficient but does the job
     *
     * @param command command to place in the file
     */
    fun addCommand(command: CMakeCommand) {
        val anchors = myAnchorsMap[command.commandType]
        // go through the list and try to satisfy conditions
        val range = IndexRange(myElements.size, 0)
        var afterHasPriority = false

        if (anchors == null) {
            // make respect firsts, lasts, and any of its own dependents
            adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false)
            adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false)
            val beforeDependents = myBeforeAnchorsMap[command.commandType]
            val afterDependents = myAfterAnchorsMap[command.commandType]
            if (beforeDependents != null) {
                // treat them as first (ie. before this node)
                adjustIndexRange(command, range, beforeDependents, AnchorType.FIRST, false)
            }
            if (afterDependents != null) {
                // treat them as last (ie. after this node)
                adjustIndexRange(command, range, afterDependents, AnchorType.LAST, false)
            }
        } else {
            for (anchor in anchors) {
                when (anchor.anchorType) {
                    AnchorType.FIRST -> adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, true)

                    AnchorType.BEFORE -> {
                        adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false)
                        adjustIndexRange(command, range, myBeforeAnchorsMap[anchor.commandAnchor], AnchorType.BEFORE, true)

                        beforeAnchorIndex(range, anchor.commandAnchor)

                        // treat any of the after of the same parent as last so this command will come before
                        adjustIndexRange(command, range, myAfterAnchorsMap[anchor.commandAnchor], AnchorType.LAST, false)
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false)
                    }

                    AnchorType.AFTER -> {
                        adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false)
                        // treat any of the before of the same parent as firsts so this command will come after
                        adjustIndexRange(command, range, myBeforeAnchorsMap[anchor.commandAnchor], AnchorType.FIRST, false)

                        if (afterAnchorIndex(range, anchor.commandAnchor)) {
                            // found true anchor, use the afterIndex for placement, the before may be too low in the file
                            afterHasPriority = true
                        }

                        adjustIndexRange(command, range, myAfterAnchorsMap[anchor.commandAnchor], AnchorType.AFTER, true)
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false)
                    }

                    AnchorType.LAST ->
                        //adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false);
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, true)
                }//adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false);
            }
        }
        if (range.beforeIndex == myElements.size && range.afterIndex == 0) {
            // no anchors, goes last
            addElement(command)
        } else if (range.beforeIndex == myElements.size) {
            // no before anchor, goes after
            addElement(range.afterIndex, command)
        } else if (range.afterIndex == 0) {
            // no after anchor, goes before
            addElement(range.beforeIndex, command)
        } else {
            if (range.beforeIndex < range.afterIndex) throw IllegalStateException("Invalid anchor definitions: BeforeAnchor(" + range.beforeIndex + ") < AfterAnchor(" + range.afterIndex + ") for " + command.commandType.name)
            // insert before
            addElement(if (afterHasPriority) range.afterIndex else range.beforeIndex, command)
        }
    }

    fun setCommand(name: String, vararg args: String): CMakeCommand? {
        return setCommand(getCommandType(name), Arrays.asList(*args))
    }

    fun setCommand(name: String, args: Collection<String>): CMakeCommand? {
        return setCommand(getCommandType(name), args)
    }

    fun setOrAddCommand(name: String, vararg args: String): CMakeCommand? {
        val commandType = getCommandType(name)
        return setOrAddCommand(commandType!!, Arrays.asList(*args))
    }

    fun setOrAddCommand(name: String, args: Collection<String>): CMakeCommand? {
        val commandType = getCommandType(name)
        return setOrAddCommand(commandType!!, args)
    }

    fun setOrAddCommand(commandType: CMakeCommandType, vararg args: String): CMakeCommand {
        return setOrAddCommand(commandType, Arrays.asList(*args))
    }

    fun removeCommand(name: String) {
        removeCommand(getCommandType(name))
    }

    fun removeCommand(commandType: CMakeCommandType?) {
        var index: Int
        do {
            index = getCommandIndex(commandType)
            if (index != -1) {
                removeElement(index)
            }
        } while (index != -1)
    }

    /**
     * Replace, modify or insert command based on command type and command anchors information
     *
     * @param commandType command name (not CMake command name but builder specific name)
     * @param args arguments for the command
     * @return resulting command (new or modified)
     */
    fun setOrAddCommand(commandType: CMakeCommandType, args: Collection<String>?): CMakeCommand {
        val command = setCommand(commandType, args)
        if (command == null) {
            // create a new one and find where to insert it
            val newCommand = CMakeCommand(commandType, args?.toList() ?: listOf())
            addCommand(newCommand)
            return newCommand
        }
        return command
    }

    /**
     * Replace or modify a command based on command type and command anchors information
     * if the command does not exist in the builder then nothing is done and null is returned.
     *
     * @param commandType command name (not CMake command name but builder specific name)
     * @param args arguments for the command
     * @return resulting command (new or modified)
     */
    fun setCommand(commandType: CMakeCommandType?, args: Collection<String>?): CMakeCommand? {
        val command = getCommand(commandType)
        var newCommand = command

        if (command != null) {

            val argList = args?.toList() ?: listOf()

            if (!commandType!!.isMultiple && commandType.maxArgs == INF_MAX_ARGS) {
                if (myElementNodeMap[command] != null) {
                    // original command, replace it with new
                    newCommand = CMakeCommand(command)
                    newCommand.commentOut(false)
                    newCommand.addAll(argList)
                    replaceElement(command, newCommand)
                } else {
                    newCommand!!.addAll(argList)
                }
            } else {
                if (commandType.isMultiple) {
                    if (commandType.isNoDupeArgs) {
                        // add another one after this one if the arg value is different
                        if (!command.allArgsEqual(argList)) {
                            newCommand = CMakeCommand(command.commandType, argList)
                            newCommand.commentOut(false)
                            addElementAfter(command, newCommand)
                        }
                    } else {
                        // add another one after this one
                        newCommand = CMakeCommand(command.commandType, argList)
                        addElementAfter(command, newCommand)
                    }
                } else {
                    if (myElementNodeMap[command] != null) {
                        // original command, replace it with new
                        newCommand = CMakeCommand(command)
                        newCommand.commentOut(false)
                        newCommand.setArgs(argList)
                        replaceElement(command, newCommand)
                    } else {
                        newCommand!!.setArgs(argList)
                    }
                }
            }
        }
        return newCommand
    }

    companion object {
        // commands can have fixed and variable arguments
        // fixed arguments can have dependency on variable arguments of other commands in the command set for a given cmake file
        // this allows fixed args to be dependent on args of other commands or values in the command set
        //
        //
        private val LOG = getInstance("com.vladsch.clionarduinoplugin.generators")

        const val INF_MAX_ARGS = 1000
        @Suppress("MemberVisibilityCanBePrivate")
        val COMMAND_REF = Pattern.compile("<\\$([a-zA-Z_$][a-zA-Z_0-9$]*)(?:\\[(\\d+)])?\\$>")!!

        /**
         * replace other commands' argument references in the given string
         *
         *
         *
         * <$COMMAND_NAME$> refers to variable arg 0 of command with name COMMAND_NAME or string mapped by COMMAND_NAME
         * <$COMMAND_NAME[2]$> refers to variable arg 2 of command with name COMMAND_NAME
         * <$COMMAND_NAME[]$> invalid, always empty result
         * <$COMMAND_NAME[-1]$> first from from the end, ie. command.getArgCount() - 1
         * <$COMMAND_NAME[-3]$> third from from the end, ie. command.getArgCount() - 3
         *
         *
         * NOTE: if Value or Command is not found or index is invalid then it is the same as the value not being empty
         *
         *
         * if command is not found or has less args then an empty value will be used
         *
         * @param arg      string with possible command references and variable value references
         * @param valueSet map of names to values, if value is CMakeCommand then its argument value will be extracted, otherwise the argument value will be the String value of passed object
         * @return string with variables replaced
         */
        fun replacedCommandParams(arg: String, valueSet: Map<String, Any>): String {
            val matcher = COMMAND_REF.matcher(arg)
            if (matcher.find()) {
                val sb = StringBuffer()
                do {
                    val commandRef = matcher.group(1)
                    val ref = valueSet[commandRef]
                    var value: String? = ""

                    if (ref != null) {
                        if (ref is CMakeCommand) {
                            val command = ref as CMakeCommand?
                            var index = 0
                            value = matcher.group(2)
                            if (value != null) {
                                index = try {
                                    Integer.parseInt(value)
                                } catch (ignored: NumberFormatException) {
                                    command!!.argCount
                                }
                            }

                            if (index < 0) index += command!!.argCount

                            value = if (index >= 0 && index < command!!.argCount) command.getArg(index) else ""
                        } else {
                            value = ref.toString()
                        }
                    }

                    matcher.appendReplacement(sb, value)
                } while (matcher.find())

                matcher.appendTail(sb)
                return sb.toString()
            }
            return arg
        }
    }
}
