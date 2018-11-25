package com.vladsch.clionarduinoplugin.util.helpers

import com.vladsch.clionarduinoplugin.resources.TemplateResolver

class VariableExpander {
    val valueMap = HashMap<String, List<String>>()

    fun resolve(value: CharSequence?): String {
        return value.resolveRefs(TemplateResolver.VARIABLE_REF) { name, _ ->
            valueMap[name]?.joinToString() ?: ""
        }
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
