package com.vladsch.clionarduinoplugin.resources

class Programmer(val id: String, val name: String) {
    val isNull: Boolean
        get() = id.isEmpty()

    companion object {
        val NULL = Programmer("", "")
    }
}
