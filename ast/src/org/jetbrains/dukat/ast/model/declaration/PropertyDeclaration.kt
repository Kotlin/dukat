package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

data class PropertyDeclaration(
        val name: String,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,
        val optional: Boolean,
        val modifiers: List<ModifierDeclaration>
) : MemberDeclaration