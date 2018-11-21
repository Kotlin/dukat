package org.jetbrains.dukat.ast

import kotlin.js.JsName

interface AstNode

interface TypeDeclaration

class SimpleTypeDeclaration(val value: Int): TypeDeclaration

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
    fun createSimpleTypeDeclaration(value: Int) = SimpleTypeDeclaration(value)
}