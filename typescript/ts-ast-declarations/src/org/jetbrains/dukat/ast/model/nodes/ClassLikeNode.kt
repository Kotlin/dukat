package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

interface ClassLikeNode : TopLevelEntity, UniqueNode {
    val name: NameEntity
    val members: List<MemberNode>
}