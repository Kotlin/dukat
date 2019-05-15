package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode

data class ObjectModel(
        val name: String,
        val members: List<MemberNode>,

        val parentEntities: List<HeritageModel>
) : ClassLikeNode, TopLevelNode
