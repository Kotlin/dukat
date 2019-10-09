package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class TypeAliasModel(
        override val name: NameEntity,
        val typeReference: TypeModel,
        val typeParameters: List<TypeParameterModel>,
        override val visibilityModifier: VisibilityModifierModel
) : TopLevelModel