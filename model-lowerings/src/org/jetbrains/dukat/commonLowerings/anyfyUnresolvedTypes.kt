package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities
import org.jetbrains.dukat.stdlib.TSLIBROOT
import org.jetbrains.dukat.stdlib.asKotlinType

private class AnyfyLowering : ModelWithOwnerTypeLowering {
    private fun TypeValueModel.anify(): TypeValueModel {
        return copy(
                value = "Any".asKotlinType(),
                params = listOf(),
                fqName = "Any".asKotlinType()
        )
    }

    private fun TypeValueModel.checkOrAnyfy(): TypeValueModel {
        val shortName = value.rightMost()
        return if ((fqName == null) && (!KotlinStdlibEntities.contains(shortName))) {
            this.anify()
        } else {
            this
        }
    }

    private fun TypeModel.checkOrAnyfy(): TypeModel {
        return when (this) {
            is TypeValueModel -> checkOrAnyfy()
            else -> this
        }
    }

    override fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val node = ownerContext.node
        val nodeResolved = node.copy(type = node.type.checkOrAnyfy())
        return super.lowerPropertyModel(ownerContext.copy(node = nodeResolved))
    }
}

class AnyfyUnresolvedTypes : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return AnyfyLowering().lowerRoot(module, NodeOwner(module, null))
    }
}