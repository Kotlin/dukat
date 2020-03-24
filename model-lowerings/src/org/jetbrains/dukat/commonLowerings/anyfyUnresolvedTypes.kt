package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities

private class AnyfyLowering : ModelWithOwnerTypeLowering {
    private fun TypeValueModel.anify(): TypeValueModel {
        return copy(
                value = IdentifierEntity("Any"),
                params = listOf(),
                fqName = IdentifierEntity("<LIBROOT>").appendLeft(IdentifierEntity("Any"))
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

private fun ModuleModel.anyfyUnresolvedTypes(): ModuleModel {
    return AnyfyLowering().lowerRoot(this, NodeOwner(this, null))
}

private fun SourceSetModel.anyfyUnresolvedTypes(): SourceSetModel {
    return transform {
        it.anyfyUnresolvedTypes()
    }
}

class AnyfyUnresolvedTypes() : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.anyfyUnresolvedTypes()
    }
}