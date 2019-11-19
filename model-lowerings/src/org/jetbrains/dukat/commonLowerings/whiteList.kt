package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel

private fun TopLevelModel.isWhiteListed(names: Set<NameEntity>): Boolean {
    return when(this) {
        is FunctionModel -> names.contains(extend?.name)
        else -> names.contains(name)
    }
}

private fun ModuleModel.whiteList(names: Set<NameEntity>): ModuleModel {
    return copy(
            declarations = declarations.filter {
                it.isWhiteListed(names)
            },
            submodules = submodules.map { it.whiteList(names) }
    )
}

fun SourceSetModel.whiteList(names: Set<NameEntity>): SourceSetModel {
    return copy(sources = sources.map { source ->
        source.copy(root = source.root.whiteList(names))
    })
}