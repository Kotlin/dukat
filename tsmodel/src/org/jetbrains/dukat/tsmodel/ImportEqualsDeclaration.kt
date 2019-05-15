package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelEntity

data class ImportEqualsDeclaration(
    val name: String,
    val moduleReference: ModuleReferenceDeclaration,
    val uid: String
) : TopLevelEntity