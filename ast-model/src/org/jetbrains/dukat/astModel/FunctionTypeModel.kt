package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.marker.TypeModel

data class FunctionTypeModel(
        val parameters: List<ParameterModel>,
        val type: TypeModel,

        val metaDescription: String?,
        var nullable: Boolean = false
) : TypeModel