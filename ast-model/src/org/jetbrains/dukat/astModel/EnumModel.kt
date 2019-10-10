package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class EnumModel(
        override val name: NameEntity,
        val values: List<EnumTokenModel>,
        override val visibilityModifier: VisibilityModifierModel
) : TopLevelModel