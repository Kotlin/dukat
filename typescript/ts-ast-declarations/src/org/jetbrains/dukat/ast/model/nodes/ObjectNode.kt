package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class ObjectNode(
        override val name: NameEntity,
        override val members: List<MemberNode>,

        val parentEntities: List<HeritageNode>,
        override val uid: String
) : ClassLikeNode
