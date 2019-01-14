package org.jetbrains.dukat.ast.model

data class MethodDeclaration (
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValue,
        val typeParameters: List<TypeParameter>,
        val operator: Boolean
) : MemberDeclaration