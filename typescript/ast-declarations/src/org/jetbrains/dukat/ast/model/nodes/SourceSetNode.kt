package org.jetbrains.dukat.ast.model.nodes

data class SourceSetNode(val sources: List<SourceFileNode>)

fun SourceSetNode.transform(rootHandler: (DocumentRootNode) -> DocumentRootNode): SourceSetNode
            = copy(sources = sources.map { source -> source.transform(rootHandler) })