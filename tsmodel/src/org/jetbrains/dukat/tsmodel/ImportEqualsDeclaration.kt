package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity

data class ImportEqualsDeclaration(
        val name: String,
        val moduleReference: NameEntity,
        val uid: String
) : TopLevelDeclaration