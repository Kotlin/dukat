package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.SourceFileModel

interface StdlibModelLowering : CommonModelLowering {

    fun lowerStdLib(sourceFile: SourceFileModel): SourceFileModel

    override fun lower(sourceFile: SourceFileModel): SourceFileModel {
        return if (sourceFile.root.shortName == IdentifierEntity("<LIBROOT>")) {
            lowerStdLib(sourceFile)
        } else {
            sourceFile
        }
    }
}