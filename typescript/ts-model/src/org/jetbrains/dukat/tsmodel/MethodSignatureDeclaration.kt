package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class MethodSignatureDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,
        val optional: Boolean,
        val modifiers: List<ModifierDeclaration>
) : MemberDeclaration, ParameterOwnerDeclaration