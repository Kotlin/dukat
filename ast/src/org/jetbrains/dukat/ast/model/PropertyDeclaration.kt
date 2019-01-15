package org.jetbrains.dukat.ast.model

data class PropertyDeclaration(
        val name: String,
        val type: ParameterValue,
        val typeParameters: List<TypeParameter>,
        val getter: Boolean,
        val setter: Boolean
) : MemberDeclaration