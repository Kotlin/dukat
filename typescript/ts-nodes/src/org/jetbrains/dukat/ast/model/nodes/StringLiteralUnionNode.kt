package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class StringLiteralUnionNode(
        val params: List<String>,

        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
): TypeNode