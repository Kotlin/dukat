package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

data class PropertyModel(
        val name: NameEntity,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: NameEntity?,
        val immutable: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean
) : MemberModel