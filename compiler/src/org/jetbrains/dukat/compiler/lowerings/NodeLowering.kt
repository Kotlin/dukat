package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface NodeLowering {
    fun lowerVariableNode(ownerContext: NodeOwner<VariableNode>): VariableNode
    fun lowerFunctionNode(ownerContext: NodeOwner<FunctionNode>): FunctionNode
    fun lowerClassNode(ownerContext: NodeOwner<ClassNode>): ClassNode
    fun lowerInterfaceNode(ownerContext: NodeOwner<InterfaceNode>): InterfaceNode

    fun lowerParameterDeclaration(ownerContext: NodeOwner<ParameterDeclaration>): ParameterDeclaration
    fun lowerTypeParameter(ownerContext: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration
    fun lowerMemberNode(ownerContext: NodeOwner<MemberNode>): MemberNode
    fun lowerTypeAliasDeclaration(ownerContext: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration
    fun lowerObjectNode(ownerContext: NodeOwner<ObjectNode>): ObjectNode

    fun lowerTypeNode(ownerContext: NodeOwner<TypeNode>): ParameterValueDeclaration
    fun lowerFunctionNode(ownerContext: NodeOwner<FunctionTypeNode>): ParameterValueDeclaration
    fun lowerUnionTypeDeclaration(ownerContext: NodeOwner<UnionTypeDeclaration>): ParameterValueDeclaration
    fun lowerUnionTypeNode(ownerContext: NodeOwner<UnionTypeNode>): ParameterValueDeclaration
    fun lowerIntersectionTypeDeclaration(ownerContext: NodeOwner<IntersectionTypeDeclaration>): ParameterValueDeclaration
    fun lowerTupleDeclaration(ownerContext: NodeOwner<TupleDeclaration>): ParameterValueDeclaration

    fun lowerParameterValue(ownerContext: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val declaration = ownerContext.node
        return when (declaration) {
            is TypeNode -> lowerTypeNode(NodeOwner(declaration, ownerContext))
            is FunctionTypeNode -> lowerFunctionNode(NodeOwner(declaration, ownerContext))
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(NodeOwner(declaration, ownerContext))
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(NodeOwner(declaration, ownerContext))
            is UnionTypeNode -> lowerUnionTypeNode(NodeOwner(declaration, ownerContext))
            is TupleDeclaration -> lowerTupleDeclaration(NodeOwner(declaration, ownerContext))
            else -> declaration
        }
    }


    fun lowerClassLikeDeclaration(ownerContext: NodeOwner<ClassLikeDeclaration>): ClassLikeDeclaration {
        val declaration = ownerContext.node
        return when (declaration) {
            is InterfaceNode -> lowerInterfaceNode(NodeOwner(declaration, ownerContext))
            is ClassNode -> lowerClassNode(NodeOwner(declaration, ownerContext))
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(ownerContext: NodeOwner<TopLevelDeclaration>): TopLevelDeclaration {
        val declaration = ownerContext.node
        return when (declaration) {
            is VariableNode -> lowerVariableNode(NodeOwner(declaration, ownerContext))
            is FunctionNode -> lowerFunctionNode(NodeOwner(declaration, ownerContext))
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(NodeOwner(declaration, ownerContext))
            is DocumentRootNode -> lowerRoot(declaration, NodeOwner(declaration, ownerContext))
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(NodeOwner(declaration, ownerContext))
            is ObjectNode -> lowerObjectNode(NodeOwner(declaration, ownerContext))
            else -> declaration.duplicate()
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>, ownerContext: NodeOwner<DocumentRootNode>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(NodeOwner(declaration, ownerContext))
        }
    }

    fun lowerRoot(documentRoot: DocumentRootNode, ownerContext: NodeOwner<DocumentRootNode>): DocumentRootNode {
        return documentRoot.copy(
                declarations = lowerTopLevelDeclarations(documentRoot.declarations, ownerContext)
        )
    }
}
