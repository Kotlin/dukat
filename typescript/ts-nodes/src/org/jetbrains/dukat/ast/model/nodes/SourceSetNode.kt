package org.jetbrains.dukat.ast.model.nodes

data class SourceSetNode(
        val sourceName: List<String>,
        val sources: List<SourceFileNode>
)

fun SourceSetNode.transform(rootHandler: (ModuleNode) -> ModuleNode): SourceSetNode
            = copy(sources = sources.map { source -> source.transform(rootHandler) })