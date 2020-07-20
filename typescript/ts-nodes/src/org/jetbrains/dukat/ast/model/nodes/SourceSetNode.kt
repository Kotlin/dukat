package org.jetbrains.dukat.ast.model.nodes

data class SourceSetNode(
        val sourceName: List<String>,
        val sources: List<SourceFileNode>
)