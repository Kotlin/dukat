package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class VariableNode(
        var name: NameEntity,
        val type: TypeNode,

        val inline: Boolean,
        val typeParameters: List<TypeValueNode>,
        val extend: ClassLikeReferenceNode?,
        override val uid: String,
        override val external: Boolean,
        val explicitlyDeclaredType: Boolean
) : TopLevelNode
