package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.astCommon.AstEntity

data class SourceFileModel(
        val fileName: String,
        val root: ModuleModel,
        val referencedFiles: List<IdentifierNode>
) : AstEntity

fun SourceFileModel.transform(rootHandler: (ModuleModel) -> ModuleModel)
    = copy(root = rootHandler(root))
