package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.stdlib.isKotlinStdlibPrefixed

fun SourceSetModel.omitStdLib(): SourceSetModel {
    val sourcesResolved = sources.filter { source ->
        !source.root.name.isKotlinStdlibPrefixed()
    }
    return copy(sources = sourcesResolved)
}