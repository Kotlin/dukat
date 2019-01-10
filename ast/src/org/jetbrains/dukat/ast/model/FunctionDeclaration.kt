package org.jetbrains.dukat.ast.model

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValue
) : Declaration {
    constructor(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValue) : this(name, parameters.toList(), type)
}