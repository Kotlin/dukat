package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.marker.TypeModel
import org.jetbrains.dukat.astCommon.Entity

data class HeritageModel(
        var value: TypeModel,
        val typeParams: List<TypeModel>,
        val delegateTo: DelegationModel?
) : Entity