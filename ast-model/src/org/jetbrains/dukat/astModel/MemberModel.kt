package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

interface MemberModel : Entity {
    val visibilityModifier: VisibilityModifierModel
}