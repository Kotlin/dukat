package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity

data class ParameterNode(
        val name: String,
        val type: TypeNode,
        val initializer: TypeValueNode?,
        val meta: String?,

        val vararg: Boolean,
        val optional: Boolean
) : Entity