package org.jetbrains.dukat.ast.model.nodes

data class ConstructorNode(
        val parameters: List<ConstructorParameterNode>,
        val typeParameters: List<TypeValueNode>,

        val generated: Boolean = false
) : MemberNode