package org.jetbrains.dukat.astModel

data class FunctionTypeModel(
        override val parameters: List<LambdaParameterModel>,
        override val type: TypeModel,

        val metaDescription: String?,
        override var nullable: Boolean = false
) : TypeModel, CallableModel