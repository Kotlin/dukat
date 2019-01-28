package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

data class MethodSignatureDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,
        val optional: Boolean,
        val modifiers: List<ModifierDeclaration>
) : MemberDeclaration