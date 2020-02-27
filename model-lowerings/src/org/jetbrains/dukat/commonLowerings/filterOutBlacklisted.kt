package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities

private fun ModuleModel.filterOutBlacklisted(): ModuleModel {
    val declarationsResolved = declarations.filter {

        val res = if (it.name.leftMost() == IdentifierEntity("<LIBROOT>")) {
            !KotlinStdlibEntities.contains(it.name.rightMost())
        } else {
            !KotlinStdlibEntities.contains(it.name)
        }

        res
    }
    return copy(declarations = declarationsResolved, submodules = submodules.map { it.filterOutBlacklisted() })
}

fun SourceSetModel.filterOutBlacklisted(): SourceSetModel {
    return transform { it.filterOutBlacklisted() }
}