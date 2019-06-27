package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

data class TypeAliasModel(
        override val name: NameEntity,
        val typeReference: TypeModel,
        val typeParameters: List<TypeParameterModel>
) : TopLevelModel