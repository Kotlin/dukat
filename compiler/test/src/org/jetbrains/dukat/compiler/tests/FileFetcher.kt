package org.jetbrains.dukat.compiler.tests

import java.io.File

class FileFetcher(private val path: String, private val postfix: String, private val outputExtension: String = ".d.kt") {

    fun fileSet(path: String, accept: (String) -> Boolean): Sequence<File> {
        return File(path).walkTopDown().filter { file ->
            accept(file.path)
        }
    }

    fun getDescriptor(path: String) = path.removeSuffix(postfix)
    fun getTarget(sourcePath: String) = sourcePath.replace(postfix, outputExtension)

    fun fileSetWithDescriptors(): Array<Array<String>> {
        val rootFolder = File(path)
        return fileSet(path) { fileName -> fileName.endsWith(postfix) }.mapNotNull { file ->

            val sourcePath = file.normalize().absolutePath
            val targetPath = getTarget(sourcePath)
            val descriptor = getDescriptor(file.relativeTo(rootFolder).path)

            if (!(file.name.startsWith("_") || file.parentFile.name.startsWith("_"))) {
                arrayOf(
                        descriptor,
                        sourcePath,
                        targetPath
                )
            } else null
        }.toList().toTypedArray()
    }
}
