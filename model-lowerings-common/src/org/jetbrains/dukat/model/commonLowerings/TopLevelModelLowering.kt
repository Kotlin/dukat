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
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern

interface TopLevelModelLowering {
    fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel): VariableModel = ownerContext.node
    fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel = ownerContext.node
    fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel? = ownerContext.node
    fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel? = ownerContext.node
    fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, parentModule: ModuleModel): TypeAliasModel = ownerContext.node

    fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>, parentModule: ModuleModel): ObjectModel = ownerContext.node
    fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>, parentModule: ModuleModel): EnumModel = ownerContext.node


    fun lowerClassLikeModel(ownerContext: NodeOwner<ClassLikeModel>, parentModule: ModuleModel): ClassLikeModel? {
        return when (val declaration = ownerContext.node) {
            is InterfaceModel -> lowerInterfaceModel(NodeOwner(declaration, ownerContext), parentModule)
            is ClassModel -> lowerClassModel(NodeOwner(declaration, ownerContext), parentModule)
            else -> declaration
        }
    }

    fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>, parentModule: ModuleModel): TopLevelModel? {
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
        return declarations.mapNotNull { declaration ->
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
