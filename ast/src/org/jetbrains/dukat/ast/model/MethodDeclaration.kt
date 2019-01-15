package org.jetbrains.dukat.ast.model

data class MethodDeclaration (
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValue,
        val typeParameters: List<TypeParameter>,
        val override: Boolean,
        val operator: Boolean
) : MemberDeclaration