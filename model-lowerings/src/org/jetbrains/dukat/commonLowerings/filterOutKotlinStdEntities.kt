package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel

private fun ModuleModel.isLib(): Boolean {
    return name == IdentifierEntity("<LIBROOT>")
}

private fun TopLevelModel.isInKotlinStdLib(): Boolean {
    return if (this is FunctionModel) {
        extend?.let { KotlinStdlibEntities.contains(it.name) } ?: false
    } else {
        KotlinStdlibEntities.contains(name)
    }
}

private fun ModuleModel.generateStdLib(): ModuleModel {
    return copy(declarations = declarations.filterNot { it.isInKotlinStdLib() })
}

fun SourceSetModel.filterOutKotlinStdEntities(): SourceSetModel {
    val sourcesResolved = sources.map { source ->
        if (source.root.isLib()) {
            source.copy(root = source.root.generateStdLib())
        } else {
            source
        }
    }
    return copy(sources = sourcesResolved)
}