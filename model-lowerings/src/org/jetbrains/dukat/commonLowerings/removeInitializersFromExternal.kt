package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.expressions.DefinedExternallyExpressionModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap

private class RemoveInitializersFromExternalsLowering : TopLevelModelLowering {
    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val declaration = ownerContext.node
        val members = declaration.members.map { lowerMemberModel(ownerContext.wrap(it)) }
        return super.lowerClassModel(ownerContext.copy(node = declaration.copy(members = members)), parentModule)
    }

    override fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>, parentModule: ModuleModel): ObjectModel {
        val declaration = ownerContext.node
        val members = declaration.members.map { lowerMemberModel(ownerContext.wrap(it)) }
        return super.lowerObjectModel(ownerContext.copy(node = declaration.copy(members = members)), parentModule)
    }

    private fun MemberModel.removeInitializerIfParentExternal(parent: CanHaveExternalModifierModel): MemberModel {
        return if (!parent.external || this !is PropertyModel || initializer == null) {
            this
        } else {
            copy(initializer = DefinedExternallyExpressionModel(initializer))
        }
    }

    private fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>): MemberModel {
        val declaration = ownerContext.node

        return when (val owner = ownerContext.owner?.node) {
            is CanHaveExternalModifierModel -> declaration.removeInitializerIfParentExternal(owner)
            else -> declaration
        }
    }
}

class RemoveInitializersFromExternals : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return RemoveInitializersFromExternalsLowering().lowerRoot(module, NodeOwner(module, null))
    }
}
