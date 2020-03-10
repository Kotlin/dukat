package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.statements.StatementModel

data class ParameterModel(
        val name: String,
        val type: TypeModel,
        // TODO: actually, initializer should always be expression,
        //  but we need to figure out what we should do with comments in initializers before we can change that.
        val initializer: StatementModel?,
        val vararg: Boolean,
        val modifier: ParameterModifierModel?
) : Entity