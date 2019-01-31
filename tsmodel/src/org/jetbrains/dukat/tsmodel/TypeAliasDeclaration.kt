package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TopLevelDeclaration

data class TypeAliasDeclaration(
        val aliasName: String,
        val typeParameters: List<TokenDeclaration>,
        val typeReference: ParameterValueDeclaration
): TopLevelDeclaration