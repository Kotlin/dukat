package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class ExportAssignmentDeclaration(
    val name: String
): TopLevelDeclaration