package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

interface TopLevelModel : KotlinModel {
    val name: NameEntity
    val visibilityModifier: VisibilityModifierModel
}