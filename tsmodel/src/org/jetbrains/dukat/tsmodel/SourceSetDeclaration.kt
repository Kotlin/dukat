package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstEntity

data class SourceSetDeclaration(
    val sources: List<SourceFileDeclaration>
) : AstEntity

