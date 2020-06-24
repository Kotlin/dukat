package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.StatementModel

data class PropertyParameterModel(
    override val name: String,
    override val type: TypeModel,
    val initializer: StatementModel?,
    val visibilityModifier: VisibilityModifierModel
): CallableParameterModel