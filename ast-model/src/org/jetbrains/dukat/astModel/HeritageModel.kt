package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity

data class HeritageModel(
        var value: TypeValueModel,
        val typeParams: List<TypeModel>,
        val delegateTo: DelegationModel?
) : Entity