package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class ObjectNode(
        val name: NameEntity,
        val members: List<MemberNode>,

        val parentEntities: List<HeritageNode>,
        val uid: String
) : ClassLikeNode
