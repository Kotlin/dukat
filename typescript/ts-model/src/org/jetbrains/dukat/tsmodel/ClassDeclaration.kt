package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity

data class ClassDeclaration(
        override val name: NameEntity,
        override val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val parentEntities: List<HeritageClauseDeclaration>,
        override val modifiers: Set<ModifierDeclaration>,
        override val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String
) : ClassLikeDeclaration, ExpressionDeclaration, WithModifiersDeclaration
