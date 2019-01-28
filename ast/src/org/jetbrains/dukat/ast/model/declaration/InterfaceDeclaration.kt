package org.jetbrains.dukat.ast.model.declaration

data class InterfaceDeclaration(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<InterfaceDeclaration>
) : ClassLikeDeclaration