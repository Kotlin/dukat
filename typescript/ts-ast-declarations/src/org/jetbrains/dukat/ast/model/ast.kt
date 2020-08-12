package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.LiteralUnionNode
import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

@Suppress("UNCHECKED_CAST")
fun <T : Entity> Entity.duplicate(): T {
    return when (this) {
        is ClassDeclaration -> copy() as T
        is EnumDeclaration -> copy() as T
        is ExpressionStatementDeclaration -> copy() as T
        is FunctionDeclaration -> copy() as T
        is FunctionTypeDeclaration -> copy() as T
        is GeneratedInterfaceReferenceDeclaration -> copy() as T
        is ImportEqualsDeclaration -> copy() as T
        is InterfaceDeclaration -> copy() as T
        is ModuleDeclaration -> copy() as T
        is ParameterDeclaration -> copy() as T
        is TypeDeclaration -> copy() as T
        is VariableDeclaration -> copy() as T

        is InterfaceNode -> copy() as T
        is LiteralUnionNode -> copy() as T
        else -> raiseConcern("can not copy ${this}") { this as T }
    }
}
