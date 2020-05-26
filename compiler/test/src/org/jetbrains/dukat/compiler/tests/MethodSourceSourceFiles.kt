package org.jetbrains.dukat.compiler.tests

import java.io.File

class MethodSourceSourceFiles(
    private val path: String,
    private val postfix: String,
    private val outputExtension: String = ".d.kt"): MethodSource
{
    fun getDescriptor(path: String) = path.removeSuffix(postfix)
    fun getTarget(sourcePath: String) = sourcePath.replace(postfix, outputExtension)

    override fun fileSetWithDescriptors(): Array<Array<String>> {
        val rootFolder = File(path)
        return fileSet(path) {
            fileName -> fileName.endsWith(postfix)
        }.filter { file ->
            !(file.name.startsWith("_") || file.parentFile.name.startsWith("_"))
        }.map { file ->
            val sourcePath = file.normalize().absolutePath
            val targetPath = getTarget(sourcePath)
            val descriptor = getDescriptor(file.relativeTo(rootFolder).path)

            val tsConfig = File(file.parent, "tsconfig.json")

            arrayOf(
                    descriptor,
                    sourcePath,
                    targetPath,
                    if (tsConfig.exists()) tsConfig.normalize().absolutePath else ""
            )
        }.toList().toTypedArray()
    }
}
