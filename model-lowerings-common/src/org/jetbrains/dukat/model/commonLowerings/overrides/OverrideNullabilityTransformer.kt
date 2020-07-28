package org.jetbrains.dukat.model.commonLowerings.overrides

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel

internal fun TypeModel.changeNullabilityToMatchChild(
    childType: TypeModel
): TypeModel {
    return when {
        this !is TypeValueModel -> {
            this
        }
        childType is TypeParameterReferenceModel -> {
            if (this.value == IdentifierEntity("Any")) {
                this.copy(nullable = true)
            } else {
                this
            }
        }
        childType is TypeValueModel -> {
            if (params.size != childType.params.size) {
                this
            } else {
                val newParams = params.zip(childType.params).map { (parentParam, childParam) ->
                    parentParam.type.changeNullabilityToMatchChild(childParam.type)
                }
                copy(params = newParams.mapIndexed { index, newParam ->
                    params[index].copy(type = newParam)
                })
            }
        }
        else -> this
    }
}