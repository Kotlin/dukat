package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class PropertyModel(
        val name: String,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,

        val static: Boolean,
        val override: Boolean,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean,
        val definedExternally: Boolean
) : MemberNode