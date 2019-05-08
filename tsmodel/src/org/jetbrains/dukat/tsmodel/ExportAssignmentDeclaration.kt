package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstTopLevelEntity

data class ExportAssignmentDeclaration(
    val name: String,
    val isExportEquals: Boolean
): AstTopLevelEntity