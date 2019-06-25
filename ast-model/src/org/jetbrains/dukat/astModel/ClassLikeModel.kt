package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode

interface ClassLikeModel : TopLevelNode {
    val members: List<MemberNode>
    val companionObject: CompanionObjectModel
}