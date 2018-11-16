package org.jetbrains.dukat.ast;

import kotlin.js.JsName


@JsName("AstNode")
class AstNode() {
    private val children: MutableList<AstNode> = mutableListOf()

    fun addChild(node: AstNode): Boolean {
        return children.add(node)
    }
}