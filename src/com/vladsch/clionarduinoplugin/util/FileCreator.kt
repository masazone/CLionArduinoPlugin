package com.vladsch.clionarduinoplugin.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException

object FileCreator {
    fun createFiles(project: Project, directory: VirtualFile, templates: Map<String, String>): Map<String, VirtualFile?> {
        ApplicationManager.getApplication().runWriteAction(object : Runnable {
            override fun run() {
                CommandProcessor.getInstance().executeCommand(project, {
                    try {
                        for ((name, content) in templates) {
                            val sketch = directory.createChildData(this, name)
                            val sketchDocument = FileDocumentManager.getInstance().getDocument(sketch)
                            if (sketchDocument != null) {
                                sketchDocument.setText(content)
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }, null, null)
            }
        })

        val results = HashMap<String, VirtualFile?>()
        for ((name, _) in templates) {
            results[name] = directory.findChild(name)
        }
        return results
    }
}
