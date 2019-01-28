package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeAliasDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

fun TypeDeclaration.isGeneric() = params.isNotEmpty()

@Suppress("UNCHECKED_CAST")
fun <T: AstNode> AstNode.duplicate(): T {
    return when (this) {
        is ClassDeclaration -> copy() as T
        is DocumentRootDeclaration -> copy() as T
        is InterfaceDeclaration -> copy() as T
        is VariableDeclaration -> copy() as T
        is FunctionDeclaration -> copy() as T
        is TypeDeclaration -> copy() as T
        is ParameterDeclaration -> copy() as T
        is FunctionTypeDeclaration -> copy() as T
        is TypeAliasDeclaration -> copy() as T
        else -> throw Exception("can not copy ${this}")
    }
}