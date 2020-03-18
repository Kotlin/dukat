package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel

interface CommonModelLowering : ModelLowering {
    fun lower(sourceFile: SourceFileModel): SourceFileModel

    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.copy(sources =  source.sources.map { sourceFile -> lower(sourceFile) })
    }
}