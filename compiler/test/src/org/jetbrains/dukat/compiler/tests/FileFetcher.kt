package org.jetbrains.dukat.compiler.tests

import java.io.File

abstract class FileFetcher {
    abstract val postfix: String

    fun fileSet(path: String, postfix: String, recursive: Boolean = false): Sequence<File> {
        val rootFolder = File(path)

        val files = if (recursive) rootFolder.walk() else rootFolder.walkTopDown()

        return files.filter { file ->
            file.path.endsWith(postfix)
        }
    }

    fun fileSetWithDescriptors(path: String, recursive: Boolean = false): Array<Array<String>> {
        val rootFolder = File(path)
        return fileSet(path, postfix, recursive).mapNotNull { file ->

            val tsPath = file.normalize().absolutePath
            val ktPath = tsPath.replace(postfix, ".d.kt")
            val descriptor = file.relativeTo(rootFolder).path.replace(path, "").replace(postfix, "")

            if (!file.name.startsWith("_")) {
                arrayOf(
                        descriptor,
                        tsPath,
                        ktPath
                )
            } else null
        }.toList().toTypedArray()
    }
}
