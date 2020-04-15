package org.jetbrains.dukat.ast.model.nodes

data class ExportAssignmentNode(
        val name: String,
        val isExportEquals: Boolean
)