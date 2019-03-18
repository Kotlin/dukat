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
    fun lowerVariableNode(owner: NodeOwner<VariableNode>): VariableNode
    fun lowerFunctionNode(owner: NodeOwner<FunctionNode>): FunctionNode
    fun lowerClassNode(owner: NodeOwner<ClassNode>): ClassNode
    fun lowerInterfaceNode(owner: NodeOwner<InterfaceNode>): InterfaceNode

    fun lowerParameterDeclaration(owner: NodeOwner<ParameterDeclaration>): ParameterDeclaration
    fun lowerTypeParameter(owner: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration
    fun lowerMemberNode(owner: NodeOwner<MemberNode>): MemberNode
    fun lowerTypeAliasDeclaration(owner: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration
    fun lowerObjectNode(owner: NodeOwner<ObjectNode>): ObjectNode

    fun lowerTypeNode(owner: NodeOwner<TypeNode>): ParameterValueDeclaration
    fun lowerFunctionNode(owner: NodeOwner<FunctionTypeNode>): ParameterValueDeclaration
    fun lowerUnionTypeDeclaration(owner: NodeOwner<UnionTypeDeclaration>): ParameterValueDeclaration
    fun lowerUnionTypeNode(owner: NodeOwner<UnionTypeNode>): ParameterValueDeclaration
    fun lowerIntersectionTypeDeclaration(owner: NodeOwner<IntersectionTypeDeclaration>): ParameterValueDeclaration
    fun lowerTupleDeclaration(owner: NodeOwner<TupleDeclaration>): ParameterValueDeclaration

    fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is TypeNode -> lowerTypeNode(owner.wrap(declaration))
            is FunctionTypeNode -> lowerFunctionNode(owner.wrap(declaration))
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(owner.wrap(declaration))
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(owner.wrap(declaration))
            is UnionTypeNode -> lowerUnionTypeNode(owner.wrap(declaration))
            is TupleDeclaration -> lowerTupleDeclaration(owner.wrap(declaration))
            else -> declaration
        }
    }


    fun lowerClassLikeDeclaration(owner: NodeOwner<ClassLikeDeclaration>): ClassLikeDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is InterfaceNode -> lowerInterfaceNode(owner.wrap(declaration))
            is ClassNode -> lowerClassNode(owner.wrap(declaration))
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(owner: NodeOwner<TopLevelDeclaration>): TopLevelDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is VariableNode -> lowerVariableNode(owner.wrap(declaration))
            is FunctionNode -> lowerFunctionNode(owner.wrap(declaration))
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(owner.wrap(declaration))
            is DocumentRootNode -> lowerRoot(declaration, owner.wrap(declaration))
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(owner.wrap(declaration))
            is ObjectNode -> lowerObjectNode(owner.wrap(declaration))
            else -> declaration.duplicate()
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>, owner: NodeOwner<DocumentRootNode>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(owner.wrap(declaration))
        }
    }

    fun lowerRoot(documentRoot: DocumentRootNode, owner: NodeOwner<DocumentRootNode>): DocumentRootNode {
        return documentRoot.copy(
                declarations = lowerTopLevelDeclarations(documentRoot.declarations, owner)
        )
    }
}
