package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern

interface ModelWithOwnerLowering {
    fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel
    fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>): FunctionModel
    fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel
    fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel

    fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel
    fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>): MemberModel
    fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>): ObjectModel
    fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>): EnumModel

    fun lowerTypeNode(ownerContext: NodeOwner<TypeModel>): TypeModel = ownerContext.node

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
            is FunctionModel -> lowerFunctionModel(NodeOwner(declaration, ownerContext))
            is ClassLikeModel -> lowerClassLikeModel(NodeOwner(declaration, ownerContext))
            is ObjectModel -> lowerObjectModel(NodeOwner(declaration, ownerContext))
            is EnumModel -> lowerEnumModel(NodeOwner(declaration, ownerContext))
            is TypeAliasModel -> declaration
            else -> raiseConcern("unknown TopeLevelDeclaration ${declaration::class.simpleName}") { declaration }
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
