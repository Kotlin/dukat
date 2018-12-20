package org.jetbrains.dukat.ast.model

import kotlin.js.JsName


interface AstNode

interface Declaration : AstNode


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

class AstFactory {
    @JsName("declareVariable")
    fun declareVariable(name: String, type: TypeDeclaration) = VariableDeclaration(name, type)

    @JsName("createFunctionDeclaration")
    fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration) = FunctionDeclaration(name, parameters, type)

    @JsName("createTypeDeclaration")
    fun createTypeDeclaration(value: String) = TypeDeclaration(value, arrayOf())

    @JsName("createGenericTypeDeclaration")
    fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>) = TypeDeclaration(value, params)

    @JsName("createParameterDeclaration")
    fun createParameterDeclaration(name: String, type: TypeDeclaration) = ParameterDeclaration(name, type)

    @JsName("createDocumentRoot")
    fun createDocumentRoot(declarations: Array<Declaration>) = DocumentRoot(declarations)
}