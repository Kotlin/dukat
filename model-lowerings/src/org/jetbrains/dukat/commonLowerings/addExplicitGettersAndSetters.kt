package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class AddExplicitGettersAndSettersTypeLowering : ModelWithOwnerTypeLowering {
    override fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val node = ownerContext.node
        val type = node.type
        val shouldHaveExplicitGettersAndSetters = (type is TypeValueModel && type.value == IdentifierEntity("dynamic"))
        return ownerContext.node.copy(
                getter = node.getter || shouldHaveExplicitGettersAndSetters,
                setter = node.setter || (shouldHaveExplicitGettersAndSetters && !ownerContext.node.immutable)
        )
    }
}

class AddExplicitGettersAndSetters : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return AddExplicitGettersAndSettersTypeLowering().lowerRoot(module, NodeOwner(module, null))
    }
}