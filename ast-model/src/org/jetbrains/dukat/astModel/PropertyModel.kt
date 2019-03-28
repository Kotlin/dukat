package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode

data class PropertyModel(
        val name: String,
        val type: TypeNode,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean,
        val definedExternally: Boolean
) : MemberNode, MemberModel