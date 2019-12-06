package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.translatorString.translate

private class ImportContext(sourceSetModel: SourceSetModel) {
    private val packageNames: MutableMap<String, NameEntity> = mutableMapOf()

    fun registerFile(fileModel: SourceFileModel) {
        if (fileModel.root.declarations.isNotEmpty()) {
            packageNames[fileModel.fileName] = fileModel.root.name
        }
    }

    fun getPackageName(fileName: String): NameEntity? {
        return packageNames[fileName]
    }

    init {
        sourceSetModel.sources.forEach { registerFile(it) }
    }
}

private fun SourceFileModel.addImportsForReferencedFiles(context: ImportContext): SourceFileModel {
    val newImports = root.imports + referencedFiles.mapNotNull {
        context.getPackageName(it)
                ?.let { packageName -> ImportModel(packageName.appendLeft(IdentifierEntity("*"))) }
        }.distinct().sortedBy { it.name.translate() }

    return copy(root = root.copy(
            imports = newImports.toMutableList()
    ))
}

fun SourceSetModel.addImportsForReferencedFiles(): SourceSetModel {
    val context = ImportContext(this)
    return copy(
            sources = sources.map { it.addImportsForReferencedFiles(context) }
    )
}
