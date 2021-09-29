package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel

interface ModelLowering : Lowering<SourceSetModel, SourceSetModel> {
    fun lower(module: ModuleModel): ModuleModel

    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.copy(sources = source.sources.map { it.copy(root = lower(it.root)) })
    }
}

fun SourceSetModel.lower(vararg lowerings: Lowering<SourceSetModel, SourceSetModel>): SourceSetModel {
    return lowerings.fold(this) { sourceSet, lowering -> lowering.lower(sourceSet) }
}