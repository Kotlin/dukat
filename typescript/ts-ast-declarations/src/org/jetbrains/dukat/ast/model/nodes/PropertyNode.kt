package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class PropertyNode(
        val name: String,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeValueNode>,

        val static: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean
) : MemberNode