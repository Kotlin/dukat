package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionTypeNode(
        override val parameters: List<ParameterNode>,
        val type: ParameterValueDeclaration,
        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : TypeNode, ParameterOwnerNode
