package org.jetbrains.dukat.ast.model.visitors

import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.astCommon.TopLevelEntity

private fun ModuleNode.visitTopLevelNode(owner: SourceFileNode, visitor: (TopLevelEntity, ModuleNode, SourceFileNode) -> Unit) {
    visitor(this, this, owner)
    declarations.forEach {
        when (it) {
            is ModuleNode -> it.visitTopLevelNode(owner, visitor)
            else -> visitor(it, this, owner)
        }
    }
}

fun SourceFileNode.visitTopLevelNode(visitor: (TopLevelEntity, ModuleNode, SourceFileNode) -> Unit) {
    root.visitTopLevelNode(this, visitor)
}

fun SourceSetNode.visitTopLevelNode(visitor: (TopLevelEntity, ModuleNode, SourceFileNode) -> Unit) {
    sources.forEach { source -> source.visitTopLevelNode(visitor) }
}
