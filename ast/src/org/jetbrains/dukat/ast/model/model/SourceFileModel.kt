package org.jetbrains.dukat.ast.model.model

import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.astCommon.Declaration

data class SourceFileModel(
        val fileName: String,
        val root: ModuleModel,
        val referencedFiles: List<IdentifierNode>
) : Declaration

fun SourceFileModel.transform(rootHandler: (ModuleModel) -> ModuleModel)
    = copy(root = rootHandler(root))
