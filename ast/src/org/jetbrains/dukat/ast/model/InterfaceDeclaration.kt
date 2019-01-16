package org.jetbrains.dukat.ast.model

data class InterfaceDeclaration(
    override val name: String,
    val members: List<MemberDeclaration>,
    override val typeParameters: List<TypeParameter>,
    val parentEntities: List<InterfaceDeclaration>
) : ClassLikeDeclaration