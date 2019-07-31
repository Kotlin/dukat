package org.jetbrains.dukat.tsmodel

data class ExportAssignmentDeclaration(
        val name: String,
        val isExportEquals: Boolean
) : TopLevelDeclaration