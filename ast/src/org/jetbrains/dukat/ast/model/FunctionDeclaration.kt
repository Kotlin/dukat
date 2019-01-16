package org.jetbrains.dukat.ast.model

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValue,
        val typeParameters: List<TypeParameter>
) : Declaration