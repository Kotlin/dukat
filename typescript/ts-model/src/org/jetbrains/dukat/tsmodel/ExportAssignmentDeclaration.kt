package org.jetbrains.dukat.tsmodel

data class ExportAssignmentDeclaration(
        val uids: List<String>,
        val isExportEquals: Boolean
)