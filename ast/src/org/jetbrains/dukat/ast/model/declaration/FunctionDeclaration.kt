package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,
        val modifiers: List<ModifierDeclaration>
) : MemberDeclaration, TopLevelDeclaration