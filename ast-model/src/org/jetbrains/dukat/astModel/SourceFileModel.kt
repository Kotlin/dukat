package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity

data class SourceFileModel(
        val fileName: String,
        val root: ModuleModel,
        val referencedFiles: List<IdentifierEntity>
) : Entity

fun SourceFileModel.transform(rootHandler: (ModuleModel) -> ModuleModel)
    = copy(root = rootHandler(root))
