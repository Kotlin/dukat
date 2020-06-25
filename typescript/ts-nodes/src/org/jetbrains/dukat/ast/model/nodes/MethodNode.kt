package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.BlockDeclaration

data class MethodNode(
        val name: String,
        override val parameters: List<ParameterNode>,
        val type: TypeNode,
        val typeParameters: List<TypeValueNode>,

        val static: Boolean,
        val operator: Boolean,
        val open: Boolean,
        val meta: MethodNodeMeta?,

        val body: BlockDeclaration?,
        val isGenerator: Boolean
) : MemberNode, ParameterOwnerNode
