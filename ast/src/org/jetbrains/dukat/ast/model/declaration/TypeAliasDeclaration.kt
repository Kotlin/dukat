package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration

data class TypeAliasDeclaration(
        val aliasName: String,
        val typeParameters: List<TypeParameterDeclaration>,
        val typeReference: ParameterValueDeclaration
): TopLevelDeclaration