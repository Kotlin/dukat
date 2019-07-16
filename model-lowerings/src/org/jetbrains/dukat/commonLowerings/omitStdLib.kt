package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.SourceSetModel

fun SourceSetModel.omitStdLib(): SourceSetModel {
    return copy(sources = sources.filter { source -> source.root.name != IdentifierEntity("<LIBROOT>") })
}