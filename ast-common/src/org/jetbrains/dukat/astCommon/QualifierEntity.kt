package org.jetbrains.dukat.astCommon

data class QualifierEntity(
        val left: NameEntity,
        val right: IdentifierEntity
) : NameEntity