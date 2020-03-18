package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class AddExplicitGettersAndSettersTypeLowering : ModelWithOwnerTypeLowering {
    override fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val type = ownerContext.node.type
        val shouldHaveExplicitGettersAndSetters =
                type.nullable || (type is TypeValueModel && type.value == IdentifierEntity("dynamic"))
        return ownerContext.node.copy(
                getter = shouldHaveExplicitGettersAndSetters,
                setter = shouldHaveExplicitGettersAndSetters && !ownerContext.node.immutable
        )
    }
}

private fun ModuleModel.addExplicitGettersAndSetters(): ModuleModel {
    return AddExplicitGettersAndSettersTypeLowering().lowerRoot(this, NodeOwner(this, null))
}

private fun SourceSetModel.addExplicitGettersAndSetters() = transform { it.addExplicitGettersAndSetters() }

class AddExplicitGettersAndSetters() : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.addExplicitGettersAndSetters()
    }
}