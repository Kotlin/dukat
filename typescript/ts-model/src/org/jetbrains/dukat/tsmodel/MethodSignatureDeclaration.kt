package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class MethodSignatureDeclaration(
        val name: String,
        override val parameters: List<ParameterDeclaration>,
        override val type: ParameterValueDeclaration,
        override val typeParameters: List<TypeParameterDeclaration>,
        val optional: Boolean,
        val modifiers: List<ModifierDeclaration>
) : MemberDeclaration, ParameterOwnerDeclaration, FunctionLikeDeclaration