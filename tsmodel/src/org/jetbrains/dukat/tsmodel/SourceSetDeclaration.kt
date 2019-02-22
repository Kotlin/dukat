package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Declaration

data class SourceSetDeclaration(
    val sources: List<SourceFileDeclaration>
) : Declaration