package org.jetbrains.dukat.model.commonLowerings.overrides

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.Variance
import org.jetbrains.dukat.model.commonLowerings.OverrideTypeChecker

private fun TypeParameterModel.toCovariant(): TypeParameterModel {
    return copy(variance = Variance.COVARIANT)
}

private fun TypeValueModel.toCovariant(indexes: Set<Int>): TypeValueModel {
    return copy(params = params.mapIndexed { index, typeParameterModel ->
        if (index in indexes) {
            typeParameterModel.toCovariant()
        } else {
            typeParameterModel
        }
    })
}

internal fun TypeModel.changeVarianceToMatchChild(
    childType: TypeModel,
    typeChecker: OverrideTypeChecker
): TypeModel {
    if (childType !is TypeValueModel || this !is TypeValueModel) {
        return this
    }
    if (params.size != childType.params.size) {
        return this
    }
    val indexesToChange = params.zip(childType.params).mapIndexedNotNull { index, (parentParam, childParam) ->
        if (with(typeChecker) { childParam.type.isOverridingReturnType(parentParam.type) && !childParam.type.isEquivalent(parentParam.type) }) {
            index
        } else {
            null
        }
    }.toSet()
    if (indexesToChange.isEmpty()) {
        return this
    }
    return toCovariant(indexesToChange)
}
