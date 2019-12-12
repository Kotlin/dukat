package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class MethodNode(
        val name: String,
        val parameters: List<ParameterNode>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeValueNode>,

        val static: Boolean,
        val operator: Boolean,
        val open: Boolean,
        val meta: MethodNodeMeta?
) : MemberNode
