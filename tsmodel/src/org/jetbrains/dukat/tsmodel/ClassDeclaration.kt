package org.jetbrains.dukat.tsmodel

data class ClassDeclaration(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val modifiers: List<ModifierDeclaration>
) : ClassLikeDeclaration
