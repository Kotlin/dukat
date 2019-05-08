package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstMemberEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class ConstructorDeclaration(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,
        val modifiers: List<ModifierDeclaration>
) : AstMemberEntity