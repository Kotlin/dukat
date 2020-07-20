package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class InterfaceNode(
        override val name: NameEntity,
        override val members: List<MemberNode>,
        val typeParameters: List<TypeValueNode>,
        val parentEntities: List<HeritageNode>,

        val generated: Boolean,
        override val uid: String,
        override val external: Boolean,

        val visibility: VisibilityNode
) : ClassLikeNode