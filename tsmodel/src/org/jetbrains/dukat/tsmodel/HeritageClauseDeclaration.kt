package org.jetbrains.dukat.tsmodel

data class HeritageClauseDeclaration(
        val name: String,
        val typeArguments: List<TokenDeclaration>,
        val extending: Boolean
) : Declaration