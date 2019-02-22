package org.jetbrains.dukat.tsmodel.converters

import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration

data class SourceFileDeclaration(
    val root: PackageDeclaration,
    val referencedFiles: List<IdentifierDeclaration>
) : Declaration
