package org.jetbrains.dukat.ast

import kotlin.js.JsName

interface AstNode {
    fun children(): Iterable<AstNode>

    fun copy(): AstNode;
}

interface TypeDeclaration : AstNode;

open class SimpleTypeDeclaration(
        val value: String,
        val params: Array<TypeDeclaration>
): TypeDeclaration {
    fun isGeneric() = params.isNotEmpty()

    override fun children(): Iterable<AstNode> {
        return params.asIterable()
    }

    override fun copy(): AstNode {
        val params = children().map { it.copy() as TypeDeclaration }
        return SimpleTypeDeclaration(value, params.toTypedArray())
    }
}

class VariableDeclaration(
        val name: String,
        val type: TypeDeclaration
): AstNode {
    override fun children(): Iterable<AstNode> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun copy() = VariableDeclaration(name, type.copy() as TypeDeclaration)
}

class DocumentRoot(val declarations: Array<VariableDeclaration> = arrayOf()): AstNode {

    constructor(declarations: List<VariableDeclaration>) : this(declarations.toTypedArray())

    override fun children(): Iterable<AstNode> {
        return declarations.asIterable();
    }

    override fun copy(): AstNode {
        val decl = mutableListOf<VariableDeclaration>()
        for (declaration in declarations) {
            decl.add(declaration.copy())
        }
        return DocumentRoot(decl)
    }
}


class AstTree(val root: DocumentRoot) {
}


class AstFactory {
    @JsName("declareVariable")
    fun declareVriable(name: String, type: TypeDeclaration) = VariableDeclaration(name, type)

    @JsName("createTypeDeclaration")
    fun createTypeDeclaration(value: String) = SimpleTypeDeclaration(value, arrayOf())

    @JsName("createGenericTypeDeclaration")
    fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>) = SimpleTypeDeclaration(value, params)
}