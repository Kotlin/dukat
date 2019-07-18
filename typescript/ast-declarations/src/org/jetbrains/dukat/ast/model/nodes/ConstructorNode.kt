package org.jetbrains.dukat.ast.model.nodes

data class ConstructorNode(
        val parameters: List<ParameterNode>,
        val typeParameters: List<TypeValueNode>,

        val generated: Boolean = false
) : MemberNode