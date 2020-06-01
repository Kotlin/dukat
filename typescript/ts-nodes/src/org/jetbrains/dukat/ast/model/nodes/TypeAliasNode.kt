package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class TypeAliasNode(
        val name: NameEntity,
        val typeReference: TypeNode,
        val typeParameters: List<TypeValueNode>,
        override val uid: String,
        override val external: Boolean
) : TopLevelNode