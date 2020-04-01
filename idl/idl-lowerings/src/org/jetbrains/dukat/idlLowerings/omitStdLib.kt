package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.stdlib.TSLIBROOT

private fun ModuleModel.isLib(): Boolean {
    return name.leftMost() == TSLIBROOT
}

fun SourceSetModel.omitStdLib(): SourceSetModel {
    val sourcesResolved = sources.filter { source ->
        !source.root.isLib()
    }
    return copy(sources = sourcesResolved)
}