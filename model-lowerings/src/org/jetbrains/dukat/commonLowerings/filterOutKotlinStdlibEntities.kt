package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities
import org.jetbrains.dukat.stdlib.isTsStdlibPrefixed

private fun ModuleModel.filterOutKotlinStdlibEntities(): ModuleModel {
    val declarationsResolved = declarations.filter {
        if (it.name.isTsStdlibPrefixed()) {
            !KotlinStdlibEntities.contains(it.name.rightMost())
        } else {
            !KotlinStdlibEntities.contains(it.name)
        }
    }
    return copy(declarations = declarationsResolved, submodules = submodules.map { it.filterOutKotlinStdlibEntities() })
}