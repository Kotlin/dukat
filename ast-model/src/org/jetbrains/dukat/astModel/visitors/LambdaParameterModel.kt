package org.jetbrains.dukat.astModel.visitors

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astModel.TypeModel

data class LambdaParameterModel(
        val name: String,
        val type: TypeModel
) : Entity
