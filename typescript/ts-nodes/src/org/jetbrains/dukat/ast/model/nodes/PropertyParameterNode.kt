package org.jetbrains.dukat.ast.model.nodes

data class PropertyParameterNode(
    override val name: String,
    override val type: TypeNode,
    override val initializer: TypeValueNode?
) : ConstructorParameterNode