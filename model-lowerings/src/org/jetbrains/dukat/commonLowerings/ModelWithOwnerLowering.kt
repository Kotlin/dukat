package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
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
    fun lowerFunctionTypeModel(ownerContext: NodeOwner<FunctionTypeModel>): FunctionTypeModel
    fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel
    fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel

    fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel
    fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>): MemberModel
    fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>): ObjectModel
    fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>): EnumModel

    @Suppress("UNCHECKED_CAST")
    fun lowerTypeModel(ownerContext: NodeOwner<TypeModel>): TypeModel {
        val declaration = ownerContext.node
        return when(declaration) {
            is FunctionTypeModel -> lowerFunctionTypeModel(ownerContext as NodeOwner<FunctionTypeModel>)
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

    fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>): TopLevelModel {
        val declaration = ownerContext.node
        return when (declaration) {
            is VariableModel -> lowerVariableModel(NodeOwner(declaration, ownerContext))
            is FunctionModel -> lowerFunctionModel(NodeOwner(declaration, ownerContext))
            is ClassLikeModel -> lowerClassLikeModel(NodeOwner(declaration, ownerContext))
            is ObjectModel -> lowerObjectModel(NodeOwner(declaration, ownerContext))
            is EnumModel -> lowerEnumModel(NodeOwner(declaration, ownerContext))
            is TypeAliasModel -> declaration
            else -> raiseConcern("unknown TopeLevelDeclaration ${declaration}") { declaration }
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelModel>, ownerContext: NodeOwner<ModuleModel>): List<TopLevelModel> {
        return declarations.map { declaration ->
            lowerTopLevelModel(NodeOwner(declaration, ownerContext))
        }
    }

    fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext),
                submodules = moduleModel.submodules.map { submodule -> lowerRoot(submodule, NodeOwner(submodule, ownerContext)) }
        )
    }
}
