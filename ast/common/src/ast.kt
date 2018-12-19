package org.jetbrains.dukat.ast

import kotlin.js.JsName

interface AstNode

interface Declaration: AstNode

data class TypeDeclaration(
        val value: String,
        val params: List<TypeDeclaration>
) {
    constructor(value: String, params: Array<TypeDeclaration>) : this(value, params.toList())
    fun isGeneric() = params.isNotEmpty()
}

data class VariableDeclaration(
        val name: String,
        val type: TypeDeclaration
): Declaration

data class ParameterDeclaration(
    val name: String,
    val type: TypeDeclaration
): Declaration {
}

data class FunctionDeclaration(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: TypeDeclaration
): Declaration {
    constructor(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): this(name, parameters.toList(), type)
}

fun AstNode.copy() : AstNode {
    return when (this) {
        is VariableDeclaration -> copy()
        is FunctionDeclaration -> copy()
        else -> throw Exception("Can not copy ${this}")
    }
}

data class DocumentRoot(
        val declarations: List<Declaration> = emptyList()
): AstNode {
    constructor(declarations: Array<Declaration>) : this(declarations.toList())
}

class AstTree(val root: DocumentRoot)

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

    @JsName("createAstTree")
    fun createAstTree(declarations: Array<Declaration>) = AstTree(DocumentRoot(declarations))
}