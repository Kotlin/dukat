package org.jetbrains.dukat.model.commonLowerings

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
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.visitors.LambdaParameterModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern

interface ModelWithOwnerLowering {
    fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel): VariableModel
    fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel
    fun lowerFunctionTypeModel(ownerContext: NodeOwner<FunctionTypeModel>): FunctionTypeModel
    fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel
    fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel
    fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, parentModule: ModuleModel): TypeAliasModel

    fun lowerLambdaParameterModel(ownerContext: NodeOwner<LambdaParameterModel>): LambdaParameterModel
    fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel
    fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>, parentModule: ModuleModel): MemberModel
    fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>, parentModule: ModuleModel): ObjectModel
    fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>, parentModule: ModuleModel): EnumModel

    fun lowerTypeParameterModel(ownerContext: NodeOwner<TypeParameterModel>): TypeParameterModel {
        val typeParameterModel = ownerContext.node
        return typeParameterModel.copy(
                type = lowerTypeModel(ownerContext.wrap(typeParameterModel.type)),
                constraints = typeParameterModel.constraints.map { lowerTypeModel(ownerContext.wrap(it)) }
        )
    }

    fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map { param -> lowerTypeParameterModel(ownerContext.wrap(param)) })
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerTypeModel(ownerContext: NodeOwner<TypeModel>): TypeModel {
        return when (val declaration = ownerContext.node) {
            is FunctionTypeModel -> lowerFunctionTypeModel(ownerContext as NodeOwner<FunctionTypeModel>)
            is TypeValueModel -> lowerTypeValueModel(ownerContext as NodeOwner<TypeValueModel>)
            else -> declaration
        }
    }

    fun lowerClassLikeModel(ownerContext: NodeOwner<ClassLikeModel>, parentModule: ModuleModel): ClassLikeModel {
        return when (val declaration = ownerContext.node) {
            is InterfaceModel -> lowerInterfaceModel(NodeOwner(declaration, ownerContext), parentModule)
            is ClassModel -> lowerClassModel(NodeOwner(declaration, ownerContext), parentModule)
            else -> declaration
        }
    }

    fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>, parentModule: ModuleModel): TopLevelModel {
        return when (val declaration = ownerContext.node) {
            is VariableModel -> lowerVariableModel(NodeOwner(declaration, ownerContext), parentModule)
            is FunctionModel -> lowerFunctionModel(NodeOwner(declaration, ownerContext), parentModule)
            is ClassLikeModel -> lowerClassLikeModel(NodeOwner(declaration, ownerContext), parentModule)
            is ObjectModel -> lowerObjectModel(NodeOwner(declaration, ownerContext), parentModule)
            is EnumModel -> lowerEnumModel(NodeOwner(declaration, ownerContext), parentModule)
            is TypeAliasModel -> lowerTypeAliasModel(NodeOwner(declaration, ownerContext), parentModule)
            else -> raiseConcern("unknown TopLevelDeclaration ${declaration}") { declaration }
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelModel>, ownerContext: NodeOwner<ModuleModel>, parentModule: ModuleModel): List<TopLevelModel> {
        return declarations.map { declaration ->
            lowerTopLevelModel(NodeOwner(declaration, ownerContext), parentModule)
        }
    }

    fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext, moduleModel),
                submodules = moduleModel.submodules.map { submodule -> lowerRoot(submodule, NodeOwner(submodule, ownerContext)) }
        )
    }
}
