package org.jetbrains.dukat.ast.model.nodes

data class PropertyNode(
        val name: String,
        val type: TypeNode,
        val typeParameters: List<TypeValueNode>,

        val static: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean
) : MemberNode