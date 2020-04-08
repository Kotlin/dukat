package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.stdlib.TSLIBROOT

interface StdlibModelLowering : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.copy(sources = source.sources.map {
            if (it.root.shortName == TSLIBROOT) {
                it.copy(root = lower(it.root))
            } else {
                it
            }
        })
    }
}