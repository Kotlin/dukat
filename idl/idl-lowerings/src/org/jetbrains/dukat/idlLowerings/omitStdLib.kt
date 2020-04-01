package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.stdlib.isTsStdlibPrefixed

fun SourceSetModel.omitStdLib(): SourceSetModel {
    val sourcesResolved = sources.filter { source ->
        !source.root.name.isTsStdlibPrefixed()
    }
    return copy(sources = sourcesResolved)
}