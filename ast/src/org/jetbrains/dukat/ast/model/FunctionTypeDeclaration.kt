package org.jetbrains.dukat.ast.model

data class FunctionTypeDeclaration(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValue,
        val nullable: Boolean = false
) : ParameterValue {
    constructor(parameters: Array<ParameterDeclaration>, type: ParameterValue) : this(
            parameters.toList(), type)
}