package org.jetbrains.dukat.ast.model

fun TypeDeclaration.isGeneric() = params.isNotEmpty()

fun AstNode.duplicate(): AstNode {
    return when (this) {
        is VariableDeclaration -> copy()
        is FunctionDeclaration -> copy()
        is TypeDeclaration -> copy()
        else -> throw Exception("Can not copy ${this}")
    }
}