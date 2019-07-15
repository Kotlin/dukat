package org.jetbrains.dukat.astModel.visitors

import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel

fun ModuleModel.visitTopLevelModel(visitor: (TopLevelModel) -> Unit) {
    visitor(this)
    declarations.forEach(visitor)
    submodules.forEach { it.visitTopLevelModel(visitor) }
}

fun SourceSetModel.visitTopLevelModel(visitor: (TopLevelModel) -> Unit) {
    sources.forEach { source -> source.root.visitTopLevelModel(visitor) }
}