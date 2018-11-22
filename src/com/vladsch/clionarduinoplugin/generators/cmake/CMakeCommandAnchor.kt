package com.vladsch.clionarduinoplugin.generators.cmake

import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType

class CMakeCommandAnchor private constructor(val anchorType: AnchorType, val commandType: CMakeCommandType, val commandAnchor: CMakeCommandType = CMakeCommandType.NULL) {

    override fun toString(): String {
        return "CMakeCommandAnchor{" +
                "" + commandType.name +
                " " + anchorType.name +
                " " + commandAnchor.name +
                '}'.toString()
    }

    companion object {

        fun first(commandType: CMakeCommandType): CMakeCommandAnchor {
            return CMakeCommandAnchor(AnchorType.FIRST, commandType)
        }

        fun last(commandType: CMakeCommandType): CMakeCommandAnchor {
            return CMakeCommandAnchor(AnchorType.LAST, commandType)
        }

        fun before(commandAnchor: CMakeCommandType, commandType: CMakeCommandType): CMakeCommandAnchor {
            return CMakeCommandAnchor(AnchorType.BEFORE, commandType, commandAnchor)
        }

        fun after(commandAnchor: CMakeCommandType, commandType: CMakeCommandType): CMakeCommandAnchor {
            return CMakeCommandAnchor(AnchorType.AFTER, commandType, commandAnchor)
        }
    }
}
