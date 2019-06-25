package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TopLevelNode

interface ClassLikeModel : TopLevelNode {
    val members: List<MemberModel>
    val companionObject: CompanionObjectModel
}