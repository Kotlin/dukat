package org.jetbrains.dukat.ast.model

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValue,
        val typeParameters: List<TypeParameter>
) : Declaration {
    constructor(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValue,
            typeParameters: Array<TypeParameter>
    ) : this(name, parameters.toList(), type, typeParameters.toList())
}