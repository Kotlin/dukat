package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity

data class LambdaParameterModel(
        override val name: String?,
        override val type: TypeModel
) : Entity, CallableParameterModel
