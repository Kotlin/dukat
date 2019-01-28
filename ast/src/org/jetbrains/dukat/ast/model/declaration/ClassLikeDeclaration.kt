package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration

interface ClassLikeDeclaration : TopLevelDeclaration {
    val name: String
    val typeParameters: List<TypeParameterDeclaration>
}