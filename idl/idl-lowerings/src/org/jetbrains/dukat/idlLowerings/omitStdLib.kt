package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel

private fun ModuleModel.isLib(): Boolean {
    return name.leftMost() == IdentifierEntity("<LIBROOT>")
}

fun SourceSetModel.omitStdLib(): SourceSetModel {
    val sourcesResolved = sources.filter { source ->
        !source.root.isLib()
    }
    return copy(sources = sourcesResolved)
}