package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstEntity

data class HeritageClauseDeclaration(
        val name: HeritageSymbolDeclaration,
        val typeArguments: List<QualifiedLeftDeclaration>,
        val extending: Boolean
) : AstEntity