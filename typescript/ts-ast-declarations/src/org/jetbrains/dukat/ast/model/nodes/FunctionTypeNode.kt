package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionTypeNode(
        val parameters: List<ParameterNode>,
        val type: TypeNode,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : TypeNode
