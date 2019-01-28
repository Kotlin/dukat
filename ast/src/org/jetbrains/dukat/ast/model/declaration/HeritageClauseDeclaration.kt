package org.jetbrains.dukat.ast.model.declaration

data class HeritageClauseDeclaration(
    val name: String,
    val typeArguments: List<TokenDeclaration>,
    val extending: Boolean
) : Declaration