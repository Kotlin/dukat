package org.jetbrains.dukat.ast.model.nodes

data class ObjectNode(
        val name: String,
        val members: List<MemberNode>,

        val parentEntities: List<HeritageNode>
) : ClassLikeNode
