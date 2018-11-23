package com.vladsch.clionarduinoplugin.util.helpers

import java.io.*

fun getResourceFiles(resourceClass: Class<*>, path: String, prefixPath: Boolean = false): List<String> {
    val filenames = ArrayList<String>()

    getResourceAsStream(resourceClass, path)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { br ->
            while (true) {
                val resource = br.readLine() ?: break
                if (prefixPath) {
                    filenames.add(path + File.separator + resource)
                } else {
                    filenames.add(resource)
                }
            }
        }
    }

    return filenames
}

fun StringBuilder.streamAppend(inputStream: InputStream) {
    BufferedReader(InputStreamReader(inputStream)).use { br ->
        while (true) {
            val resource = br.readLine() ?: break
            this.append(resource).append('\n')
        }
    }
}

fun getResourceAsString(resourceClass: Class<*>, path: String): String {
    val sb = StringBuilder()

    getResourceAsStream(resourceClass, path)?.use { inputStream ->
        sb.streamAppend(inputStream)
    }

    return sb.toString()
}

fun getFileContent(file: File): String {
    val inputStream = FileInputStream(file)
    val sb = StringBuilder()
    sb.streamAppend(inputStream)
    return sb.toString()
}

fun getResourceAsStream(resourceClass: Class<*>, resource: String): InputStream? {
    try {
        val inputStream = resourceClass.getResourceAsStream(resource)
        inputStream.available()
        return inputStream
    } catch (e: Exception) {

    }
    return null
}

fun File.isChildOf(ancestor: File): Boolean {
    return (path + File.separator).startsWith(ancestor.path + File.separator)
}

val File.nameOnly: String
    get() {
        val pos = name.lastIndexOf('.')
        return if (pos > 0 && pos > name.lastIndexOf(File.separatorChar)) name.substring(0, pos) else name
    }

val File.dotExtension: String
    get() {
        val pos = name.lastIndexOf('.')
        return if (pos > 0 && pos > name.lastIndexOf(File.separatorChar)) name.substring(pos) else ""
    }

val File.pathSlash: String
    get() {
        val pos = path.lastIndexOf(File.separatorChar)
        return if (pos != -1) path.substring(0, pos + 1) else ""
    }

operator fun File.plus(name: String): File {
    val path = this.path
    val dbDir = File(if (!path.endsWith(File.separatorChar) && !name.startsWith(File.separatorChar)) path + File.separator + name else "$path$name")
    return dbDir
}

fun String?.extractLeadingDigits(): Pair<Int?, String> {
    if (this == null) return Pair(null, "")

    val text = this
    var value: Int? = null
    var start = 0
    for (i in 0 until text.length) {
        val c = text[i]
        if (!c.isDigit()) break
        val digit = c - '0'
        value = (value ?: 0) * 10 + digit
        start = i + 1
    }
    return Pair(value, text.substring(start))
}

