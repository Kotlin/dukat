package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.AstNode
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


fun ValueTypeNode.isGeneric() = params.isNotEmpty()

fun ParameterValueDeclaration.makeNullable(): ParameterValueDeclaration {
    return when (this) {
        is ValueTypeNode -> copy(nullable = true)
        is TypeDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        is FunctionTypeNode -> copy(nullable = true)
        is UnionTypeDeclaration -> copy(nullable = true)
        is QualifiedNamedDeclaration -> copy(nullable = true)
        is GeneratedInterfaceReferenceDeclaration -> copy(nullable = true)
        is ObjectLiteralDeclaration -> copy(nullable = true)
        else -> throw Exception("makeNullable does not recognize type ${this}")
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : AstNode> AstNode.duplicate(): T {
    return when (this) {
        is EnumNode -> copy() as T
        is EnumDeclaration -> copy() as T
        is ImportEqualsDeclaration -> copy() as T
        is ExportAssignmentDeclaration -> copy() as T
        is ClassDeclaration -> copy() as T
        is ObjectNode -> copy() as T
        is PackageDeclaration -> copy() as T
        is InterfaceDeclaration -> copy() as T
        is InterfaceNode -> copy() as T
        is VariableDeclaration -> copy() as T
        is VariableNode -> copy() as T
        is FunctionDeclaration -> copy() as T
        is FunctionNode -> copy() as T
        is TypeDeclaration -> copy() as T
        is ValueTypeNode -> copy() as T
        is ParameterDeclaration -> copy() as T
        is FunctionTypeDeclaration -> copy() as T
        is TypeAliasDeclaration -> copy() as T
        is GeneratedInterfaceReferenceDeclaration -> copy() as T
        else -> throw Exception("can not copy ${this}")
    }
}