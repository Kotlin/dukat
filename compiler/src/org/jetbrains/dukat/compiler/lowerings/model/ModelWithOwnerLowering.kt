package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration

interface ModelWithOwnerLowering {
    fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel
    fun lowerFunctionNode(ownerContext: NodeOwner<FunctionNode>): FunctionNode
    fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel
    fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel

    fun lowerParameterDeclaration(ownerContext: NodeOwner<ParameterDeclaration>): ParameterDeclaration
    fun lowerTypeParameter(ownerContext: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration
    fun lowerMemberNode(ownerContext: NodeOwner<MemberNode>): MemberNode
    fun lowerObjectNode(ownerContext: NodeOwner<ObjectNode>): ObjectNode
    fun lowerEnumNode(ownerContext: NodeOwner<EnumNode>): EnumNode


    fun lowerTypeNode(ownerContext: NodeOwner<ValueTypeNode>): ParameterValueDeclaration
    fun lowerFunctionTypeNode(ownerContext: NodeOwner<FunctionTypeNode>): ParameterValueDeclaration
    fun lowerUnionTypeNode(ownerContext: NodeOwner<UnionTypeNode>): ParameterValueDeclaration
    fun lowerTupleDeclaration(ownerContext: NodeOwner<TupleDeclaration>): ParameterValueDeclaration

    fun lowerParameterValue(ownerContext: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val declaration = ownerContext.node
        return when (declaration) {
            is ValueTypeNode -> lowerTypeNode(NodeOwner(declaration, ownerContext))
            is FunctionTypeNode -> lowerFunctionTypeNode(NodeOwner(declaration, ownerContext))
            is UnionTypeNode -> lowerUnionTypeNode(NodeOwner(declaration, ownerContext))
            is TupleDeclaration -> lowerTupleDeclaration(NodeOwner(declaration, ownerContext))
            else -> declaration
        }
    }


    fun lowerClassLikeModel(ownerContext: NodeOwner<ClassLikeModel>): ClassLikeModel {
        val declaration = ownerContext.node
        return when (declaration) {
            is InterfaceModel -> lowerInterfaceModel(NodeOwner(declaration, ownerContext))
            is ClassModel -> lowerClassModel(NodeOwner(declaration, ownerContext))
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(ownerContext: NodeOwner<TopLevelNode>): TopLevelNode {
        val declaration = ownerContext.node
        return when (declaration) {
            is VariableModel -> lowerVariableModel(NodeOwner(declaration, ownerContext))
            is FunctionNode -> lowerFunctionNode(NodeOwner(declaration, ownerContext))
            is ClassLikeModel -> lowerClassLikeModel(NodeOwner(declaration, ownerContext))
            is ObjectNode -> lowerObjectNode(NodeOwner(declaration, ownerContext))
            is EnumNode -> lowerEnumNode(NodeOwner(declaration, ownerContext))
            else -> throw Exception("unknown TopeLevelDeclaration ${ownerContext::class.simpleName}")
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelNode>, ownerContext: NodeOwner<ModuleModel>): List<TopLevelNode> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(NodeOwner(declaration, ownerContext))
        }
    }

    fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext),
                sumbodules = moduleModel.sumbodules.map { submodule -> lowerRoot(submodule, NodeOwner(submodule, ownerContext)) }
        )
    }
}
