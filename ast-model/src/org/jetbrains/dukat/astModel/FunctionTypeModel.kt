package org.jetbrains.dukat.astModel

data class FunctionTypeModel(
        val parameters: List<ParameterModel>,
        val type: TypeModel,

        val metaDescription: String?,
        var nullable: Boolean = false
) : TypeModel