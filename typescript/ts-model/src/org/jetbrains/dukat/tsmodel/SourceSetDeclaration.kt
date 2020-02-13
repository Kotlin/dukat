package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity

data class SourceSetDeclaration(
    val sourceName: List<String>,
    val sources: List<SourceFileDeclaration>
) : Entity

