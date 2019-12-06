package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
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

private fun MemberModel.withoutMeta(): MemberModel {
    return when (this) {
        is MethodModel -> copy(
                parameters = parameters.map { it.withoutMeta() }
        )
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

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        val node = ownerContext.node.copy(members = filterOutConflictingOverloads(ownerContext.node.members))
        return super.lowerInterfaceModel(ownerContext.copy(node = node))
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        val node = ownerContext.node.copy(members = filterOutConflictingOverloads(ownerContext.node.members))
        return super.lowerClassModel(ownerContext.copy(node = node))
    }
}

fun ModuleModel.removeConflictingOverloads(): ModuleModel {
    return ConflictingOverloads().lowerRoot(this, NodeOwner(this, null))
}

fun SourceFileModel.removeConflictingOverloads() = copy(root = root.removeConflictingOverloads())
fun SourceSetModel.removeConflictingOverloads() =
        copy(sources = sources.map(SourceFileModel::removeConflictingOverloads))
