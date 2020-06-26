package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel

interface ModuleModelLowering {
    fun lower(module: ModuleModel): ModuleModel
}

interface ModelLowering : Lowering<SourceSetModel, SourceSetModel>, ModuleModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.copy(sources = source.sources.map { it.copy(root = lower(it.root)) })
    }
}

interface ComposableModelLowering : ModelLowering {
    val lowerings: List<ModuleModelLowering>

    override fun lower(module: ModuleModel): ModuleModel {
        return lowerings.fold(module) { m, lowering -> lowering.lower(m)  }
    }
}

fun SourceSetModel.lower(vararg lowerings: ModelLowering): SourceSetModel {
    return lowerings.fold(this) { sourceSet, lowering -> lowering.lower(sourceSet) }
}