package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MetaData

data class FunctionTypeNode(
        val parameters: List<ParameterNode>,
        val type: TypeNode,
        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : TypeNode
