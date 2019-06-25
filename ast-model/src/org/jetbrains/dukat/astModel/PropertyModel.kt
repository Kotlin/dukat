package org.jetbrains.dukat.astModel

data class PropertyModel(
        val name: String,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean
) : MemberModel