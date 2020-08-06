package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableNode(
        var name: NameEntity,
        val type: ParameterValueDeclaration,

        val inline: Boolean,
        val typeParameters: List<TypeValueNode>,
        val extend: ClassLikeReferenceNode?,
        override val uid: String,
        override val external: Boolean,
        val explicitlyDeclaredType: Boolean
) : TopLevelNode
