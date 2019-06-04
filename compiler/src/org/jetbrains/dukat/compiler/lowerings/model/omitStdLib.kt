package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.SourceSetModel

fun SourceSetModel.omitStdLib(): SourceSetModel {
    return copy(sources = sources.filter { source -> source.root.packageName != IdentifierEntity("<LIBROOT>") })
}