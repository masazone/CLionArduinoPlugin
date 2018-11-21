package com.vladsch.clionarduinoplugin.resources

class Board(val id: String, val name: String, val cpuList: HashMap<String, String>) {
    private val nameCpuMap: HashMap<String, String> by lazy {
        val map = HashMap<String, String>()
        for ((key, value) in cpuList) {
            map[value] = key
        }
        map
    }

    val isNull: Boolean
        get() = id.isEmpty()

    fun cpuIdByName(cpuName: String?): String {
        return nameCpuMap[cpuName] ?: ""
    }

    fun cpuIdById(cpuId: String?): String {
        return if (cpuId != null && cpuList.containsKey(cpuId)) cpuId else ""
    }

    fun cpuNameById(cpuId: String?): String {
        return cpuList[cpuId] ?: ""
    }

    fun cpuNameByName(cpuName: String?): String {
        return if (cpuName != null && nameCpuMap.containsKey(cpuName)) cpuName else ""
    }

    companion object {
        val NULL = Board("", "", HashMap())
    }
}
