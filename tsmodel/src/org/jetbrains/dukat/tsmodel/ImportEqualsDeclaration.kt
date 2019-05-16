package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

data class ImportEqualsDeclaration(
    val name: String,
    val moduleReference: NameEntity,
    val uid: String
) : TopLevelEntity