package org.jetbrains.dukat.ast.model

interface ClassLikeDeclaration : Declaration {
    val name: String
    val typeParameters: List<TypeParameter>
}