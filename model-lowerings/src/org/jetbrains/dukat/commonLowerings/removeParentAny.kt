package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.TSLIBROOT

private fun TypeValueModel.isAny() = fqName == QualifierEntity(TSLIBROOT, IdentifierEntity("Any"))

private fun ClassLikeModel.removeParentAny(): List<HeritageModel> {
    return parentEntities.filter { !it.value.isAny() }
}

private class RemoveParentAnyLowering : TopLevelModelLowering {
    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel? {
        val node = ownerContext.node
        return super.lowerClassModel(ownerContext.copy(node = node.copy(parentEntities = node.removeParentAny())), parentModule)
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel? {
        val node = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = node.copy(parentEntities = node.removeParentAny())), parentModule)
    }
}

class RemoveParentAny : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return RemoveParentAnyLowering().lowerRoot(module, NodeOwner(module, null))
    }
}