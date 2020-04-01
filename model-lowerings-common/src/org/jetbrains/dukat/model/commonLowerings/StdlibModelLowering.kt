package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.stdlib.TSLIBROOT

interface StdlibModelLowering : CommonModelLowering {

    fun lowerStdLib(sourceFile: SourceFileModel): SourceFileModel

    override fun lower(sourceFile: SourceFileModel): SourceFileModel {
        return if (sourceFile.root.shortName == TSLIBROOT) {
            lowerStdLib(sourceFile)
        } else {
            sourceFile
        }
    }
}