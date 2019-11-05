package org.jetbrains.dukat.astModel

data class TypeParameterModel(
        val type: TypeModel,
        val constraints: List<TypeModel>,
        val variance: Variance = Variance.INVARIANT,

        override val nullable: Boolean = false
) : TypeModel

enum class Variance {
    INVARIANT, COVARIANT, CONTRAVARIANT
}