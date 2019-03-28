package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class ParameterModel(
        val name: String,
        val type: TypeNode,
        val initializer: ExpressionDeclaration?,

        val vararg: Boolean,
        val optional: Boolean
) : Declaration