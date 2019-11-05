package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

data class TypeParameterReferenceModel(
    val name: NameEntity,

    val metaDescription: String?,
    override val nullable: Boolean = false
) : TypeModel