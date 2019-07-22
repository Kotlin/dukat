package org.jetbrains.dukat.astModel

data class FunctionTypeModel(
        val parameters: List<ParameterModel>,
        val type: TypeModel,

        val metaDescription: String?,
        override var nullable: Boolean = false
) : TypeModel