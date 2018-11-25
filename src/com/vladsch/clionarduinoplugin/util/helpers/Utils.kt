package com.vladsch.clionarduinoplugin.util.helpers

import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType
import java.util.regex.Pattern

fun <T> Boolean.ifElse(trueVal: T, falseVal: T): T {
    return if (this) trueVal else falseVal
}

fun <T> Boolean.ifElse(trueVal: () ->T, falseVal: ()->T): T {
    return if (this) trueVal.invoke() else falseVal.invoke()
}

fun CharSequence?.resolveRefs(pattern: Pattern, resolver: (name: String, index: String?) -> String?): String {
    if (this == null) return ""

    val matcher = pattern.matcher(this)
    if (matcher.find()) {
        val sb = StringBuffer()
        do {
            val name = matcher.group(1)
            val index:String? = if (matcher.groupCount() >= 2) matcher.group(2) else null

            val resolved = resolver.invoke(name, index)
            matcher.appendReplacement(sb, resolved ?: "")
        } while (matcher.find())

        matcher.appendTail(sb)
        return sb.toString()
    }
    return this.toString()
}

fun simpleRefResolver(values: Map<String, String>): (String, String?) -> String? {
    return { name1, index1 ->
        if (index1 != null) null
        else values[name1]
    }
}

