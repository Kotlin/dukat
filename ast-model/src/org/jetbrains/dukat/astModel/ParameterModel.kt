package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astModel.statements.StatementModel

data class ParameterModel(
        override val name: String,
        override val type: TypeModel,
        // TODO: actually, initializer should always be expression,
        //  but we need to figure out what we should do with comments in initializers before we can change that.
        val initializer: StatementModel?,
        val vararg: Boolean,
        val modifier: ParameterModifierModel?
) : Entity, CallableParameterModel