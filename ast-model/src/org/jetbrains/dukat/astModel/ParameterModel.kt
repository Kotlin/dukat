package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity

data class ParameterModel(
        val name: String,
        val type: TypeModel,
        val initializer: TypeModel?,

        val vararg: Boolean,
        val optional: Boolean
) : Entity