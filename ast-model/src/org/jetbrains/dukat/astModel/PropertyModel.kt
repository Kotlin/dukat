package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.MemberNode

data class PropertyModel(
        val name: String,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean
) : MemberNode, MemberModel