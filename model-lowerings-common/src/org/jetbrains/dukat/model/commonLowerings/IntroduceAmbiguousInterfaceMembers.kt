package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext
import org.jetbrains.dukat.ownerContext.NodeOwner

private class AmbiguousInterfaceLowering(private val modelContext: ModelContext, private val inheritanceContext: InheritanceContext) : ModelWithOwnerTypeLowering {

    private fun MemberModel.asKey(): NameEntity? {
        return when (this) {
            is PropertyModel -> name
            else -> null
        }
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val node = ownerContext.node
        val parents = modelContext.getParents(node)

        if (parents.size < 2) {
            return super.lowerInterfaceModel(ownerContext, parentModule)
        }

        val bucket = parents.flatMap { it.classLike.members }.groupBy { it.asKey() }

        val ownMembers = node.members.mapNotNull { it.asKey() }
        val membersResolved = node.members + bucket.mapNotNull { (name, members) ->
            if ((name != null) && (members.size > 1) && !ownMembers.contains(name)) {
                members.firstOrNull()
            } else {
                null
            }
         }

        return super.lowerInterfaceModel(ownerContext.copy(node = node.copy(members = membersResolved)), parentModule)
    }
}

class IntroduceAmbiguousInterfaceMembers(modelContext: ModelContext, inheritanceContext: InheritanceContext) : ModuleModelContextAwareLowering(modelContext, inheritanceContext) {
    override fun lower(module: ModuleModel): ModuleModel {
        return AmbiguousInterfaceLowering(modelContext, inheritanceContext).lowerRoot(module, NodeOwner(module, null))
    }
}