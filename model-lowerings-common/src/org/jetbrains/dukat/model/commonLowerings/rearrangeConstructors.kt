package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private class RearrangeConstructorsLowering : TopLevelModelLowering {
    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val node = ownerContext.node
        val (constructors, members) = node.members.partition { it is ConstructorModel }

        val ownerContextResolved =  if (constructors.size == 1) {
            val classWithPrimaryConstructor = ownerContext.node.copy(primaryConstructor = constructors.firstOrNull() as? ConstructorModel, members = members)
            ownerContext.copy(node = classWithPrimaryConstructor)
        } else {
            ownerContext
        }

        return super.lowerClassModel(ownerContextResolved, parentModule)
    }
}

class RearrangeConstructors : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return RearrangeConstructorsLowering().lowerRoot(module, NodeOwner(module, null))
    }
}