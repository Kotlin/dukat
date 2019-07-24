package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class TypeValueModel(
        val value: NameEntity,
        val params: List<TypeModel>,

        val metaDescription: String?,
        override val nullable: Boolean = false
) : Entity, TypeModel

fun TypeValueModel.isGeneric() = params.isNotEmpty()