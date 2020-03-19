package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity

interface ClassLikeNode : TopLevelNode, UniqueNode {
    val name: NameEntity
    val members: List<MemberNode>
}