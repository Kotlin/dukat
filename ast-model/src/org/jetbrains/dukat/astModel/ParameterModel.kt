package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astModel.statements.StatementModel

data class ParameterModel(
        val name: String,
        val type: TypeModel,
        val initializer: StatementModel?,
        val vararg: Boolean,
        val modifier: ParameterModifierModel?
) : Entity