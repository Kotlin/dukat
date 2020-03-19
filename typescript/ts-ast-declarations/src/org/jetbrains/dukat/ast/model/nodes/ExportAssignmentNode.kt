package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.TopLevelNode

data class ExportAssignmentNode(
    val name: String,
    val isExportEquals: Boolean
) : TopLevelNode {
    override val external: Boolean
        get() = throw Exception("Irrelevant")
}