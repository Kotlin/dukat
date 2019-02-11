package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class PropertyNode(
        val name: String,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,

        var owner: ClassLikeNode,
        val static: Boolean,
        val override: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean,
        val definedExternally: Boolean
) : MemberDeclaration, MemberNode