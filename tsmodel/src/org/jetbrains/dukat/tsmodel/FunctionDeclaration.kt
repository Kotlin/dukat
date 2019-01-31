package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TopLevelDeclaration

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,
        val modifiers: List<ModifierDeclaration>
) : MemberDeclaration, TopLevelDeclaration