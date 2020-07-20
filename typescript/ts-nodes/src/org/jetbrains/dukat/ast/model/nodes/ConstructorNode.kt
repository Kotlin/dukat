package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.BlockDeclaration

data class ConstructorNode(
        override val parameters: List<ParameterNode>,
        val typeParameters: List<TypeValueNode>,
        val body: BlockDeclaration?,
        override val visibility: VisibilityNode
) : MemberNode, ParameterOwnerNode