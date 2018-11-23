@file:Suppress("MemberVisibilityCanBePrivate")

package com.vladsch.clionarduinoplugin.resources

import com.vladsch.clionarduinoplugin.generators.cmake.CMakeListsTxtBuilder
import com.vladsch.clionarduinoplugin.util.helpers.*
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.BasedSequenceImpl
import java.io.File
import java.util.regex.Pattern
import javax.print.attribute.IntegerSyntax

object TemplateResolver {
    const val BUNDLED_TEMPLATES = "/com/vladsch/clionarduinoplugin/templates"

    fun getTemplatesDirectory(): File {
        val resource = TemplateResolver.javaClass.getResource(BUNDLED_TEMPLATES)
        return File(resource.toURI())
    }

    /**
     * return map of file path relative to templateType dir -> template content
     */
    fun getTemplates(templateType: String): Map<String, String> {
        val baseDir = getTemplatesDirectory()
        val templateDir = baseDir + templateType
        val templateFiles = ArrayList<File>()

        getTemplateFiles(baseDir, baseDir, templateFiles)

        val templates = HashMap<String, String>()

        // resolve all templates to actual content
        templateFiles.forEach { templates[it.path] = getContent(it) }

        val result = HashMap<String, String>()

        // now resolve based on content inheritance but only for directory requested
        for (file in templateFiles) {
            if (file.isChildOf(templateDir)) {
                var content = templates[file.path] ?: ""

                result[file.relativeTo(templateDir).path] = content

                if (content.isEmpty()) {
                    // try to resolve it using back path
                    var parentDir = file.parentFile
                    while (parentDir != null) {
                        parentDir = parentDir.parentFile

                        var starMatch: File? = null
                        var nameMatch: File? = null

                        for (parentTemplate in templateFiles) {
                            if (parentTemplate.parentFile == parentDir) {
                                if (parentTemplate.extension == file.extension) {
                                    if (parentTemplate.name == "@@" + if (file.extension.isEmpty()) "" else "." + file.extension) {
                                        starMatch = parentTemplate
                                    } else if (parentTemplate.name == file.name) {
                                        nameMatch = parentTemplate
                                    }

                                    if (starMatch != null && nameMatch != null) break;
                                }
                            }
                        }

                        if (nameMatch == null) nameMatch = starMatch
                        if (nameMatch != null) {

                            content = templates[nameMatch.path] ?: ""
                            if (!content.isEmpty()) {
                                templates[file.path] = content   // just in case there is a sub directory with an empty template that would match
                                result[file.relativeTo(templateDir).path] = content
                                break
                            }
                        }
                    }
                }
            }
        }

        return result
    }

    fun getTemplateFiles(baseDir: File, templateDir: File, files: ArrayList<File>) {
        if (templateDir.isDirectory) {
            for (resource in templateDir.list()) {
                val resourceFile = templateDir + resource
                if (resourceFile.isDirectory) {
                    getTemplateFiles(baseDir, resourceFile, files)
                } else {
                    files.add(resourceFile)
                }
            }
        } else {
            val resourceFiles = getResourceFiles(this.javaClass, templateDir.path)
            if (resourceFiles.isEmpty()) {
                files.add(templateDir)
            } else {
                for (resource in resourceFiles) {
                    val resourceFile = templateDir + resource
                    getTemplateFiles(baseDir, resourceFile, files)
                }
            }
        }
    }

    fun getContent(file: File): String {
        return if (file.exists()) getFileContent(file) else getResourceAsString(this.javaClass, file.path)
    }

    fun resolveRefs(text: CharSequence?, pattern: Pattern, resolver: (name: String, index: String?) -> String?): String {
        if (text == null) return ""

        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val sb = StringBuffer()
            do {
                val name = matcher.group(1)
                val index = matcher.group(2)

                val resolved = resolver.invoke(name, index)
                matcher.appendReplacement(sb, resolved ?: "")
            } while (matcher.find())

            matcher.appendTail(sb)
            return sb.toString()
        }
        return text.toString()
    }

    fun simpleRefResolver(values: Map<String, String>): (String, String?) -> String? {
        val simpleResolver: (String, String?) -> String? = { name1, index1 ->
            if (index1 != null) null
            else values[name1]
        }
        return simpleResolver
    }

    val COMMAND_REF = Pattern.compile("<@([a-zA-Z_$][a-zA-Z_0-9$]*)(?:\\[([^]]*)])?@>")!!
    val FILE_NAME_REF = Pattern.compile("@([a-zA-Z_$][a-zA-Z_0-9$]*)(?:\\[([^]]*)])?@")!!

    fun resolveTemplates(templates: Map<String, String>, values: Map<String, String>): Map<String, String> {
        val result = HashMap<String, String>();
        val simpleResolver = simpleRefResolver(values)

        for ((templatePath, content) in templates) {
            val file = File(templatePath)
            val fileNameOnly = file.nameOnly
            val resolvedFile = resolveRefs(fileNameOnly, FILE_NAME_REF, simpleResolver)
            if (!resolvedFile.isEmpty()) {
                val lines = BasedSequenceImpl.of(content).split('\n', Integer.MAX_VALUE-2, BasedSequence.SPLIT_INCLUDE_DELIMS)
                val template = StringBuilder()

                for (line in lines) {
                    var skip = false
                    val resolved = resolveRefs(line, COMMAND_REF) { name,index->
                        if (name == "DELETE_IF_BLANK") {
                            skip = resolveRefs(index, COMMAND_REF, simpleResolver).isEmpty()
                            ""
                        } else {
                            if (index != null) null
                            else values[name]
                        }
                    }
                    if (!skip) template.append(resolved)
                }
                result[file.pathSlash + resolvedFile + file.dotExtension] = template.toString()
            }
        }
        return result
    }
}
