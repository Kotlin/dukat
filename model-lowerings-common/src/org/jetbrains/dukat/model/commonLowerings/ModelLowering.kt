package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astModel.SourceSetModel

interface ModelLowering: Lowering<SourceSetModel, SourceSetModel>

fun SourceSetModel.lower(vararg lowerings: ModelLowering): SourceSetModel {
    return lowerings.fold(this) { sourceSet, lowering ->  lowering.lower(sourceSet) }
}