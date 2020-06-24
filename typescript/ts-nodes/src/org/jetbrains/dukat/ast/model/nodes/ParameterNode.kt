package org.jetbrains.dukat.ast.model.nodes

data class ParameterNode(
        override val name: String,
        override val type: TypeNode,
        override val initializer: TypeValueNode?,
        val meta: String?,

        val vararg: Boolean,
        val optional: Boolean
) : ConstructorParameterNode