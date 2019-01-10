package org.jetbrains.dukat.ast.model

fun TypeDeclaration.isGeneric() = params.isNotEmpty()

@Suppress("UNCHECKED_CAST")
fun <T:AstNode> AstNode.duplicate(): T {
    return when (this) {
        is VariableDeclaration -> copy() as T
        is FunctionDeclaration -> copy() as T
        is TypeDeclaration -> copy() as T
        is ParameterDeclaration -> copy() as T
        is FunctionTypeDeclaration -> copy() as T
        else -> throw Exception("can not copy ${this}")
    }
}