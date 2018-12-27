package org.jetbrains.dukat.ast


interface AstNode

interface Declaration : AstNode {
    val reflectionType
        get() = AstReflectionType.UNKNOWN_DECLARATION
}


data class TypeDeclaration(
        val value: String,
        val params: List<TypeDeclaration>
) : Declaration {
    constructor(value: String, params: Array<TypeDeclaration>) : this(value, params.toList())
}

fun TypeDeclaration.isGeneric() = params.isNotEmpty()

data class ParameterDeclaration(
        val name: String,
        val type: TypeDeclaration
) : Declaration

data class VariableDeclaration(
        val name: String,
        val type: TypeDeclaration
) : Declaration

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: TypeDeclaration
) : Declaration {
    constructor(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration) : this(name, parameters.toList(), type)
}


data class DocumentRoot(
        val declarations: List<Declaration> = emptyList()
) : Declaration {
    constructor(declarations: Array<Declaration>) : this(declarations.toList())
}


fun AstNode.duplicate(): AstNode {
    return when (this) {
        is VariableDeclaration -> copy()
        is FunctionDeclaration -> copy()
        is TypeDeclaration -> copy()
        else -> throw Exception("Can not copy ${this}")
    }
}