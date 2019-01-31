package org.jetbrains.dukat.tsmodel

data class InterfaceDeclaration(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>
) : ClassLikeDeclaration