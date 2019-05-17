package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class QualifiedNode(
        val left: NameEntity,
        val right: IdentifierEntity
) : NameEntity