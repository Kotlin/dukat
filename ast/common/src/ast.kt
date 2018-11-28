package org.jetbrains.dukat.ast

import kotlin.js.JsName

interface AstNode {
}

interface CopyAs<T> {
    fun copy(): T
}

interface WithChildren<T> {
    fun children(): Iterable<T>
}

interface TypeDeclaration : Declaration, CopyAs<TypeDeclaration>;

open class SimpleTypeDeclaration(
        val value: String,
        val params: Array<TypeDeclaration>
): TypeDeclaration, WithChildren<TypeDeclaration> {
    fun isGeneric() = params.isNotEmpty()

    override fun children(): Iterable<TypeDeclaration> {
        return params.asIterable()
    }

    override fun copy(): TypeDeclaration {
        val params = children().map { it.copy() }
        return SimpleTypeDeclaration(value, params.toTypedArray())
    }
}

interface Declaration: AstNode

class VariableDeclaration(
        val name: String,
        val type: TypeDeclaration
): Declaration, CopyAs<VariableDeclaration> {
    override fun copy() = VariableDeclaration(name, type.copy())
}

class ParameterDeclaration(
    val name: String,
    val type: TypeDeclaration
): Declaration, CopyAs<ParameterDeclaration> {
    override fun copy() = ParameterDeclaration(name, type)
}

class FunctionDeclaration(
        val name: String,
        val parameters: Array<ParameterDeclaration>,
        val type: TypeDeclaration
): Declaration, CopyAs<FunctionDeclaration> {
    override fun copy(): FunctionDeclaration {
        return FunctionDeclaration(name, parameters.map { it.copy() }.toTypedArray(), type)
    }
}

class DocumentRoot(
        val declarations: Array<Declaration> = arrayOf()
): AstNode, CopyAs<DocumentRoot>, WithChildren<Declaration> {

    constructor(declarations: List<Declaration>) : this(declarations.toTypedArray())

    override fun children(): Iterable<Declaration> {
        return declarations.asIterable()
    }

    override fun copy(): DocumentRoot {
        return copy {declaration ->
            when (declaration) {
                is VariableDeclaration -> declaration.copy()
                is FunctionDeclaration -> declaration.copy()
                else -> null
            }
        }
    }

    fun copy(transform: (declaration: Declaration) -> Declaration?): DocumentRoot {
        val decl = mutableListOf<Declaration>()
        for (declaration in declarations) {
            val transformedDeclaration = transform(declaration)
            if (transformedDeclaration != null) {
                decl.add(transformedDeclaration)
            } else {
                if (declaration is CopyAs<*>) {
                    decl.add(declaration.copy() as Declaration)
                } else {
                    throw Exception("Only copyable declarations are allowed")
                }
            }
        }

        return DocumentRoot(decl)
    }
}


class AstTree(val root: DocumentRoot) {
}


class AstFactory {
    @JsName("declareVariable")
    fun declareVariable(name: String, type: TypeDeclaration) = VariableDeclaration(name, type)

    @JsName("createFunctionDeclaration")
    fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration) = FunctionDeclaration(name, parameters, type)

    @JsName("createTypeDeclaration")
    fun createTypeDeclaration(value: String) = SimpleTypeDeclaration(value, arrayOf())

    @JsName("createGenericTypeDeclaration")
    fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>) = SimpleTypeDeclaration(value, params)

    @JsName("createParameterDeclaration")
    fun createParameterDeclaration(name: String, type: TypeDeclaration) = ParameterDeclaration(name, type)

    @JsName("createAstTree")
    fun createAstTree(declarations: Array<Declaration>) = AstTree(DocumentRoot(declarations))
}