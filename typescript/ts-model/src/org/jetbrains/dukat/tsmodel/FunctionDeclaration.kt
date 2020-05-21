package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionDeclaration(
        val name: String,
        override val parameters: List<ParameterDeclaration>,
        override val type: ParameterValueDeclaration,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val modifiers: Set<ModifierDeclaration>,
        val body: BlockDeclaration?,
        override val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String,
        val isGenerator: Boolean
) : MemberDeclaration, StatementDeclaration, WithUidDeclaration, WithModifiersDeclaration, ParameterOwnerDeclaration,
    ExpressionDeclaration, FunctionLikeDeclaration, MergeableDeclaration
