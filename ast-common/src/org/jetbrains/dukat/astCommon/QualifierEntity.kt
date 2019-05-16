package org.jetbrains.dukat.astCommon

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class QualifierEntity(
        val left: NameEntity,
        val right: IdentifierEntity
) : NameEntity