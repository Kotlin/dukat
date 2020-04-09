package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ClassDeclaration(
        override val name: NameEntity,
        override val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val parentEntities: List<HeritageClauseDeclaration>,
        override val modifiers: List<ModifierDeclaration>,
        override val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String
) : ClassLikeDeclaration, ExpressionDeclaration, WithModifiersDeclaration, MergeableDeclaration
