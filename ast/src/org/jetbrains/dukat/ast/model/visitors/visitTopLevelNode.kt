package org.jetbrains.dukat.ast.model.visitors

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.astCommon.TopLevelEntity

private fun DocumentRootNode.visitTopLevelNode(owner: SourceFileNode, visitor: (TopLevelEntity, DocumentRootNode, SourceFileNode) -> Unit) {
    visitor(this, this, owner)
    declarations.forEach {
        when (it) {
            is DocumentRootNode -> it.visitTopLevelNode(owner, visitor)
            else -> visitor(it, this, owner)
        }
    }
}

fun SourceFileNode.visitTopLevelNode(visitor: (TopLevelEntity, DocumentRootNode, SourceFileNode) -> Unit) {
    root.visitTopLevelNode(this, visitor)
}

fun SourceSetNode.visitTopLevelNode(visitor: (TopLevelEntity, DocumentRootNode, SourceFileNode) -> Unit) {
    sources.forEach { source -> source.visitTopLevelNode(visitor) }
}
