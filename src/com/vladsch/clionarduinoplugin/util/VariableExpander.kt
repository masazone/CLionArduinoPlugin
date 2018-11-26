package com.vladsch.clionarduinoplugin.util

import com.vladsch.clionarduinoplugin.resources.TemplateResolver
import com.vladsch.clionarduinoplugin.util.helpers.resolveRefs

class VariableExpander {
    val valueMap = HashMap<String, List<String>>()
    val asMacroMap = HashSet<String>()

    fun resolve(value: CharSequence?): String {
        return value.resolveRefs(TemplateResolver.VARIABLE_REF) { name, _ ->
            valueMap[name]?.joinToString() ?: if (asMacroMap.contains(name)) "\${$name}" else ""
        }
    }

    fun asMacro(name:String) {
        asMacroMap.add(name)
    }

    fun hasVariableRef(text:CharSequence?): Boolean {
        return TemplateResolver.VARIABLE_REF.matcher(text ?: "").find()
    }

    operator fun set(name:String, value: String?) {
        if (value == null) {
            valueMap.remove(name)
        } else {
            valueMap[name] = listOf(value)
        }
    }

    operator fun set(name:String, value: List<String>) {
        valueMap[name] = value
    }

    operator fun set(name:String, value: Array<String>) {
        valueMap[name] = value.toList()
    }

    operator fun get(name:String):List<String> = valueMap[name] ?: listOf()
}
