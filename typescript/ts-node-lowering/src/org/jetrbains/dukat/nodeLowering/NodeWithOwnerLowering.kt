package org.jetrbains.dukat.nodeLowering

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TypeEntity
import org.jetbrains.dukat.ownerContext.NodeOwner

interface NodeWithOwnerLowering<T : TypeEntity> {
    fun lowerVariableNode(owner: NodeOwner<VariableNode>): VariableNode
    fun lowerFunctionNode(owner: NodeOwner<FunctionNode>): FunctionNode
    fun lowerClassNode(owner: NodeOwner<ClassNode>): ClassNode
    fun lowerInterfaceNode(owner: NodeOwner<InterfaceNode>): InterfaceNode

    fun lowerParameterNode(owner: NodeOwner<ParameterNode>): ParameterNode
    fun lowerTypeParameter(owner: NodeOwner<TypeValueNode>): TypeValueNode
    fun lowerMemberNode(owner: NodeOwner<MemberNode>): MemberNode
    fun lowerTypeAliasNode(owner: NodeOwner<TypeAliasNode>): TypeAliasNode
    fun lowerObjectNode(owner: NodeOwner<ObjectNode>): ObjectNode

    fun lowerTypeValueNode(owner: NodeOwner<TypeValueNode>): T
    fun lowerFunctionTypeNode(owner: NodeOwner<FunctionTypeNode>): T
    fun lowerUnionTypeNode(owner: NodeOwner<UnionTypeNode>): T

    fun lowerClassLikeNode(owner: NodeOwner<ClassLikeNode>): ClassLikeNode {
        val declaration = owner.node
        return when (declaration) {
            is InterfaceNode -> lowerInterfaceNode(owner.wrap(declaration))
            is ClassNode -> lowerClassNode(owner.wrap(declaration))
            is ObjectNode -> lowerObjectNode(owner.wrap(declaration))
            else -> declaration
        }
    }

    fun lowerTopLevelEntity(owner: NodeOwner<TopLevelNode>): TopLevelNode {
        val declaration = owner.node
        return when (declaration) {
            is VariableNode -> lowerVariableNode(owner.wrap(declaration))
            is FunctionNode -> lowerFunctionNode(owner.wrap(declaration))
            is ClassLikeNode -> lowerClassLikeNode(owner.wrap(declaration))
            is ModuleNode -> lowerRoot(declaration, owner.wrap(declaration))
            is TypeAliasNode -> lowerTypeAliasNode(owner.wrap(declaration))
            else -> declaration.duplicate()
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelNode>, owner: NodeOwner<ModuleNode>): List<TopLevelNode> {
        return declarations.map { declaration ->
            lowerTopLevelEntity(owner.wrap(declaration))
        }
    }

    fun lowerRoot(moduleNode: ModuleNode, owner: NodeOwner<ModuleNode>): ModuleNode {
        return moduleNode.copy(
                declarations = lowerTopLevelDeclarations(moduleNode.declarations, owner)
        )
    }
}
