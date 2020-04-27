package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun TypeModel.withoutMeta(): TypeModel {
    return when (this) {
        is TypeValueModel -> copy(metaDescription = null)
        else -> this
    }
}

private fun ParameterModel.withoutMeta(): ParameterModel {
    return copy(type = type.withoutMeta())
}

private fun MemberModel.normalize(): MemberModel {
    return when (this) {
        is MethodModel -> copy(
                parameters = parameters.map { it.withoutMeta() },
                override = null
        )
        else -> this
    }
}

private fun TopLevelModel.normalize(): TopLevelModel {
    return when (this) {
        is FunctionModel -> copy(parameters = parameters.map { it.withoutMeta() })
        else -> this
    }
}

private fun filterOutConflictingOverloads(members: List<MemberModel>): List<MemberModel> {
    return members.groupBy { it.normalize() }.map { (_, bucketMembers) ->
        if (bucketMembers.size > 1) {
            bucketMembers.first().normalize()
        } else {
            bucketMembers.first()
        }
    }
}

private class ConflictingOverloads() : ModelWithOwnerTypeLowering {

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val node = ownerContext.node.copy(members = filterOutConflictingOverloads(ownerContext.node.members))
        return super.lowerInterfaceModel(ownerContext.copy(node = node), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val node = ownerContext.node.copy(members = filterOutConflictingOverloads(ownerContext.node.members))
        return super.lowerClassModel(ownerContext.copy(node = node), parentModule)
    }
}

class RemoveConflictingOverloads : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        val declarationsResolved = module.declarations.groupBy { it.normalize() }.map { (_, bucketMembers) ->
            if (bucketMembers.size > 1) {
                bucketMembers.first().normalize()
            } else {
                bucketMembers.first()
            }
        }

        val moduleResolved = module.copy(declarations = declarationsResolved)
        return ConflictingOverloads().lowerRoot(moduleResolved, NodeOwner(moduleResolved, null))
    }
}