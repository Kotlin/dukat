package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,
        val modifiers: List<ModifierDeclaration>,
        val body: BlockDeclaration?,
        override val uid: String
) : MemberDeclaration, TopLevelDeclaration, WithUidDeclaration, ParameterOwnerDeclaration, ExpressionDeclaration