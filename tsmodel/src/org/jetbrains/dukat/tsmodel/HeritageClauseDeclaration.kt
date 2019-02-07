package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Declaration

data class HeritageClauseDeclaration(
        val name: HeritageSymbolDeclaration,
        val typeArguments: List<TokenDeclaration>,
        val extending: Boolean
) : Declaration