package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity

data class ObjectModel(
        override val name: NameEntity,
        val members: List<MemberNode>,

        val parentEntities: List<HeritageModel>
) : ClassLikeNode, TopLevelNode
