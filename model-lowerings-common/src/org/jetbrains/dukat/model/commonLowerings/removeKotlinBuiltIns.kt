package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.stdlib.KotlinBuiltInEntities

private fun ModuleModel.removeKotlinBuiltIns(): ModuleModel {
    return copy(declarations = declarations.filter {
        if (it is ClassLikeModel) {
            !KotlinBuiltInEntities.contains(it.name)
        } else {
            true
        }
    })
}

private fun SourceFileModel.removeKotlinBuiltIns(): SourceFileModel {
    return copy(root = root.removeKotlinBuiltIns())
}

class RemoveKotlinBuiltIns() : StdlibModelLowering {
    override fun lowerStdLib(sourceFile: SourceFileModel): SourceFileModel {
        return sourceFile.removeKotlinBuiltIns()
    }
}