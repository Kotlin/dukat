package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astModel.visitors.LambdaParameterModel

data class FunctionTypeModel(
        val parameters: List<LambdaParameterModel>,
        val type: TypeModel,

        val metaDescription: String?,
        override var nullable: Boolean = false
) : TypeModel