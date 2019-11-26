package org.jetrbains.dukat.nodeLowering

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.TypeEntity

interface Lowering<T : TypeEntity> {
    fun lowerVariableNode(declaration: VariableNode): VariableNode
    fun lowerFunctionNode(declaration: FunctionNode): FunctionNode
    fun lowerClassNode(declaration: ClassNode): ClassNode
    fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode
    fun lowerEnumNode(declaration: EnumNode, owner: DocumentRootNode): EnumNode = declaration

    fun lowerParameterNode(declaration: ParameterNode): ParameterNode
    fun lowerTypeParameter(declaration: TypeValueNode): TypeValueNode
    fun lowerMemberNode(declaration: MemberNode): MemberNode
    fun lowerTypeAliasNode(declaration: TypeAliasNode, owner: DocumentRootNode): TypeAliasNode
    fun lowerObjectNode(declaration: ObjectNode): ObjectNode

    fun lowerTypeNode(declaration: TypeValueNode): T
    fun lowerFunctionNode(declaration: FunctionTypeNode): T
    fun lowerUnionTypeNode(declaration: UnionTypeNode): T

    fun lowerClassLikeNode(declaration: ClassLikeNode, owner: DocumentRootNode): ClassLikeNode {
        return when (declaration) {
            is InterfaceNode -> lowerInterfaceNode(declaration)
            is ClassNode -> lowerClassNode(declaration)
            is ObjectNode -> lowerObjectNode(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelEntity(declaration: TopLevelEntity, owner: DocumentRootNode): TopLevelEntity {
        return when (declaration) {
            is VariableNode -> lowerVariableNode(declaration)
            is FunctionNode -> lowerFunctionNode(declaration)
            is ClassLikeNode -> lowerClassLikeNode(declaration, owner)
            is DocumentRootNode -> lowerDocumentRoot(declaration)
            is TypeAliasNode -> lowerTypeAliasNode(declaration, owner)
            is EnumNode -> lowerEnumNode(declaration, owner)
            else -> declaration.duplicate()
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelEntity>, owner: DocumentRootNode): List<TopLevelEntity> {
        return declarations.map { declaration ->
            lowerTopLevelEntity(declaration, owner)
        }
    }

    fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        return documentRoot.copy(
                declarations = lowerTopLevelDeclarations(documentRoot.declarations, documentRoot)
        )
    }
}
