package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class TypeParameterModel(
        val name: NameEntity,
        val constraints: List<TypeModel>
) : Entity