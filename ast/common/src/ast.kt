package org.jetbrains.dukat.ast

import kotlin.js.JsName

interface AstNode

interface TypeDeclaration

open class SimpleTypeDeclaration(
        val value: String,
        val params: Array<TypeDeclaration>
): TypeDeclaration {
    fun isGeneric() = params.isNotEmpty()
}

class VariableDeclaration(
        val name: String,
        val type: TypeDeclaration
): AstNode {

}

class DocumentRoot(val declarations: Array<VariableDeclaration> = arrayOf()): AstNode

class AstTree(val root: DocumentRoot) {
}


class AstFactory {
    @JsName("createSimpleTypeDeclaration")
    fun createSimpleTypeDeclaration(value: String) = SimpleTypeDeclaration(value, arrayOf())

    @JsName("createGenericTypeDeclaration")
    fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>) = SimpleTypeDeclaration(value, params)
}