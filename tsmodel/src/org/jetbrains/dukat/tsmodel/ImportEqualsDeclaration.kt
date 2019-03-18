package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class ImportEqualsDeclaration(
    val name: String,
    val moduleReference: ModuleReferenceDeclaration,
    val uid: String
) : TopLevelDeclaration