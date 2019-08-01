package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class StatementCallModel(
        val value: NameEntity,
        val params: List<IdentifierEntity>?,
        val typeParameters: List<IdentifierEntity> = listOf(),
        override val metaDescription: String? = null
) : StatementModel
