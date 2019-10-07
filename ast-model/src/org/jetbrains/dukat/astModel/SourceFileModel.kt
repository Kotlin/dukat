package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class SourceFileModel(
        val name: NameEntity?,
        val fileName: String,
        val root: ModuleModel,
        val referencedFiles: List<String>
) : Entity

fun SourceFileModel.transform(rootHandler: (ModuleModel) -> ModuleModel) = copy(root = rootHandler(root))
