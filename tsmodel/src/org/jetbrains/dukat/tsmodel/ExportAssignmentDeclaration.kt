package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelEntity

data class ExportAssignmentDeclaration(
    val name: String,
    val isExportEquals: Boolean
): TopLevelEntity