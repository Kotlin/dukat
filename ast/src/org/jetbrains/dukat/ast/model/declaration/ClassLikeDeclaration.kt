package org.jetbrains.dukat.ast.model.declaration

interface ClassLikeDeclaration : Declaration {
    val name: String
    val typeParameters: List<TypeParameterDeclaration>
}