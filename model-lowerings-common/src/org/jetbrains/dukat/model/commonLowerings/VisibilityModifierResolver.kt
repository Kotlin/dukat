package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

@FunctionalInterface
interface VisibilityModifierResolver {
    fun resolve(): VisibilityModifierModel
}