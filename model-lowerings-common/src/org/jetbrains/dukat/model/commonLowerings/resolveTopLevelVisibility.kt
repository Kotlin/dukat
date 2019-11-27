package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.ownerContext.NodeOwner

private class ModifyVisibility(private val visibility: VisibilityModifierModel) : ModelWithOwnerLowering {
    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel {
        return ownerContext.node.copy(visibilityModifier = visibility)
    }

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>): FunctionModel {
        return ownerContext.node.copy(visibilityModifier = visibility)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        return ownerContext.node.copy(visibilityModifier = visibility)
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        return ownerContext.node.copy(visibilityModifier = visibility)
    }

    override fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>): ObjectModel {
        return ownerContext.node.copy(visibilityModifier = visibility)
    }

    override fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>): EnumModel {
        return ownerContext.node.copy(visibilityModifier = visibility)
    }

    override fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, moduleOwner: ModuleModel): TypeAliasModel {
        return ownerContext.node.copy(visibilityModifier = visibility)
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel = ownerContext.node

    override fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>): MemberModel = ownerContext.node
    override fun lowerFunctionTypeModel(ownerContext: NodeOwner<FunctionTypeModel>): FunctionTypeModel = ownerContext.node
}

private fun ModuleModel.resolveTopLevelVisibility(visibilityModifierResolver: VisibilityModifierResolver): ModuleModel {
    return ModifyVisibility(visibilityModifierResolver.resolve()).lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetModel.resolveTopLevelVisibility(visibilityModifierResolver: VisibilityModifierResolver): SourceSetModel = transform { it.resolveTopLevelVisibility(visibilityModifierResolver) }
