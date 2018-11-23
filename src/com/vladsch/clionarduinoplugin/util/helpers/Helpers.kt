package com.vladsch.clionarduinoplugin.util.helpers

import java.io.*

fun getResourceFiles(resourceClass: Class<*>, path: String, prefixPath: Boolean = false): List<String> {
    val filenames = ArrayList<String>()

    getResourceAsStream(resourceClass, path)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { br ->
            while (true) {
                val resource = br.readLine() ?: break
                if (prefixPath) {
                    filenames.add("$path/$resource")
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
    return "$path/".startsWith("${ancestor.path}/")
}

val File.nameOnly: String
    get() {
        val pos = name.lastIndexOf('.')
        return if (pos <= 0) name else name.substring(0, pos)
    }

val File.dotExtension: String
    get() {
        val pos = name.lastIndexOf('.')
        return if (pos > 0) name.substring(pos) else ""
    }

val File.pathSlash: String
    get() {
        val pos = path.lastIndexOf('/')
        return if (pos != -1) path.substring(0, pos + 1) else ""
    }

operator fun File.plus(name: String): File {
    val path = this.path
    val dbDir = File(if (!path.endsWith('/') && !name.startsWith('/')) "$path/$name" else "$path$name")
    return dbDir
}

fun File.ensureExistingDirectory(paramName: String = "directory"): File {
    if (!this.exists() || !this.isDirectory) {
        throw IllegalArgumentException("$paramName '${this.path}' must point to existing directory")
    }
    return this
}

fun File.ensureCreateDirectory(paramName: String = "directory"): File {
    if (!this.exists()) {
        if (!this.mkdir()) {
            throw IllegalStateException("could not create directory $paramName '${this.path}' must point to existing directory")
        }
    }
    if (!this.isDirectory) {
        throw IllegalStateException("$paramName '${this.path}' exists and is not a directory")
    }
    return this
}

fun String.versionCompare(other: String): Int {
    val theseParts = this.removePrefix("V").split('_', limit = 4)
    val otherParts = other.removePrefix("V").split('_', limit = 4)

    val iMax = Math.min(theseParts.size, otherParts.size)
    for (i in 0 until iMax) {
        if (i < 3) {
            // use integer compare
            val thisVersion = theseParts[i].toInt()
            val otherVersion = otherParts[i].toInt()
            if (thisVersion != otherVersion) {
                return thisVersion.compareTo(otherVersion)
            }
        } else {
            return theseParts[i].compareTo(otherParts[i])
        }
    }

    return when {
        theseParts.size > iMax -> 1
        otherParts.size > iMax -> -1
        else -> 0
    }
}

fun getVersionDirectory(dbDir: File, dbVersion: String, createDir: Boolean?): File {
    if (createDir != null) {
        dbDir.ensureExistingDirectory("dbDir")
    }

    val dbVersionDir = dbDir + dbVersion

    if (createDir == true) {
        dbVersionDir.ensureCreateDirectory("dbDir/dbVersion")
    } else if (createDir == false) {
        dbVersionDir.ensureExistingDirectory("dbDir/dbVersion")
    }
    return dbVersionDir
}

fun String.toSnakeCase(): String {
    var lastWasUpper = true
    val sb = StringBuilder()
    for (i in 0 until length) {
        val c = this[i]
        if (c.isUpperCase()) {
            if (!lastWasUpper) {
                sb.append('_')
            }
            sb.append(c.toLowerCase())
            lastWasUpper = true
        } else {
            lastWasUpper = false
            sb.append(c)
        }
    }
    return sb.toString()
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

