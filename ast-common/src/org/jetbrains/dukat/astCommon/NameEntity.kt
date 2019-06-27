package org.jetbrains.dukat.astCommon

sealed class NameEntity : Entity

data class IdentifierEntity(
        val value: String
) : NameEntity()

data class QualifierEntity(
        val left: NameEntity,
        val right: IdentifierEntity
) : NameEntity()