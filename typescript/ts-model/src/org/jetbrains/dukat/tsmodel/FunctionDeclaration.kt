package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionDeclaration(
        val name: String,
        override val parameters: List<ParameterDeclaration>,
        override val type: ParameterValueDeclaration,
        override val typeParameters: List<TypeParameterDeclaration>,
        val modifiers: List<ModifierDeclaration>,
        val body: BlockDeclaration?,
        override val uid: String
) : MemberDeclaration, TopLevelDeclaration, WithUidDeclaration, ParameterOwnerDeclaration, ExpressionDeclaration, FunctionLikeDeclaration
