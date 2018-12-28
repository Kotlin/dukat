package org.jetbrains.dukat.ast.model

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: TypeDeclaration
) : Declaration {
    constructor(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration) : this(name, parameters.toList(), type)
}