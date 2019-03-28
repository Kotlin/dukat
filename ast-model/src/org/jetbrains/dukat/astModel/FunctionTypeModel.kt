package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TypeNode

data class FunctionTypeModel(
        val parameters: List<ParameterModel>,
        val type: TypeNode,

        val metaDescription: String?,
        var nullable: Boolean = false
) : TypeNode