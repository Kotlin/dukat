package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstEntity

data class SourceFileNode(
        val fileName: String,
        val root: DocumentRootNode,
        val referencedFiles: List<IdentifierNode>
) : AstEntity


fun SourceFileNode.transform(rootHandler: (DocumentRootNode) -> DocumentRootNode)
    = copy(root = rootHandler(root))
