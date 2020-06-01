package org.jetbrains.dukat.ast.model.nodes

data class ExportAssignmentNode(
        val uids: List<String>,
        val isExportEquals: Boolean
)