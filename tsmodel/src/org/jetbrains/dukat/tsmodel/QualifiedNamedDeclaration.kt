package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity

data class QualifiedNamedDeclaration(
        val left: NameEntity,
        val right: IdentifierDeclaration
) : NameEntity