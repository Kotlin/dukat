package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class TypeParameterModel(
        val type: TypeModel,
        val constraints: List<TypeModel>,
        val variance: Variance = Variance.INVARIANT
) : Entity

enum class Variance {
    INVARIANT, COVARIANT, CONTRAVARIANT
}