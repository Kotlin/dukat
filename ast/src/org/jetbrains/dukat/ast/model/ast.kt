package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.astCommon.AstNode
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


fun TypeDeclaration.isGeneric() = params.isNotEmpty()

fun ParameterValueDeclaration.makeNullable(): ParameterValueDeclaration {
    return when (this) {
        is TypeDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        is UnionTypeDeclaration -> copy(nullable = true)
        else -> throw Exception("makeNullable does not recognize type ${this}")
    }
}

@Suppress("UNCHECKED_CAST")
fun <T: AstNode> AstNode.duplicate(): T {
    return when (this) {
        is ClassDeclaration -> copy() as T
        is DocumentRootDeclaration -> copy() as T
        is InterfaceDeclaration -> copy() as T
        is InterfaceNode -> copy() as T
        is VariableDeclaration -> copy() as T
        is FunctionDeclaration -> copy() as T
        is TypeDeclaration -> copy() as T
        is ParameterDeclaration -> copy() as T
        is FunctionTypeDeclaration -> copy() as T
        is TypeAliasDeclaration -> copy() as T
        else -> throw Exception("can not copy ${this}")
    }
}