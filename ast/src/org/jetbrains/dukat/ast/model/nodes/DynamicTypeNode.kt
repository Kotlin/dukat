package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class DynamicTypeNode(
        val projectedType: ParameterValueDeclaration,

        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration