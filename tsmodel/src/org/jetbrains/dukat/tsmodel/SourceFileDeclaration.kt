package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity

data class SourceFileDeclaration(
        val fileName: String,
        val root: PackageDeclaration,
        val referencedFiles: List<IdentifierEntity>
) : Entity
