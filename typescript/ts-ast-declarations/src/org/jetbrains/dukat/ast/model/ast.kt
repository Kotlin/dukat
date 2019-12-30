package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

fun ParameterValueDeclaration.makeNullable(): ParameterValueDeclaration {
    return when (this) {
        is TypeValueNode -> copy(nullable = true)
        is TypeParameterNode -> copy(nullable = true)
        is TypeDeclaration -> copy(nullable = true)
        is TypeParamReferenceDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        is FunctionTypeNode -> copy(nullable = true)
        is UnionTypeDeclaration -> copy(nullable = true)
        is UnionTypeNode -> copy(nullable = true)
        is GeneratedInterfaceReferenceNode -> copy(nullable = true)
        is GeneratedInterfaceReferenceDeclaration -> copy(nullable = true)
        is ObjectLiteralDeclaration -> copy(nullable = true)
        else -> raiseConcern("makeNullable does not recognize type ${this}") { this }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Entity> Entity.duplicate(): T {
    return when (this) {
        is ClassDeclaration -> copy() as T
        is EnumDeclaration -> copy() as T
        is EnumNode -> copy() as T
        is ExportAssignmentDeclaration -> copy() as T
        is ExpressionStatementDeclaration -> copy() as T
        is FunctionDeclaration -> copy() as T
        is FunctionNode -> copy() as T
        is FunctionTypeDeclaration -> copy() as T
        is FunctionTypeNode -> copy() as T
        is GeneratedInterfaceReferenceDeclaration -> copy() as T
        is GeneratedInterfaceReferenceNode -> copy() as T
        is ImportEqualsDeclaration -> copy() as T
        is InterfaceDeclaration -> copy() as T
        is InterfaceNode -> copy() as T
        is ObjectNode -> copy() as T
        is ModuleDeclaration -> copy() as T
        is ParameterDeclaration -> copy() as T
        is TupleTypeNode -> copy() as T
        is TypeAliasNode -> copy() as T
        is TypeDeclaration -> copy() as T
        is TypeValueNode -> copy() as T
        is TypeParameterNode -> copy() as T
        is UnionTypeNode -> copy() as T
        is VariableDeclaration -> copy() as T
        is VariableNode -> copy() as T
        else -> raiseConcern("can not copy ${this}") { this as T }
    }
}