package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.Declaration

data class ParameterModel(
        val name: String,
        val type: TypeNode,
        val initializer: TypeNode?,

        val vararg: Boolean,
        val optional: Boolean
) : Declaration