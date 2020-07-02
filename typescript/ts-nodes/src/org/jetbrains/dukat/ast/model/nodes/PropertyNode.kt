package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class PropertyNode(
        val name: String,
        val type: TypeNode,
        val typeParameters: List<TypeValueNode>,

        val static: Boolean,

        val initializer: ExpressionDeclaration?,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean,

        val hasType: Boolean
) : MemberNode