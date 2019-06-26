package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TopLevelNode

interface ClassLikeModel : TopLevelNode, MemberModel {
    val members: List<MemberModel>
    val companionObject: CompanionObjectModel
}