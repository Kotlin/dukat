package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity

data class SourceFileNode(
        val fileName: String,
        val root: DocumentRootNode,
        val referencedFiles: List<IdentifierEntity>
) : Entity


fun SourceFileNode.transform(rootHandler: (DocumentRootNode) -> DocumentRootNode)
    = copy(root = rootHandler(root))
