package org.jetbrains.dukat.astModel

interface ClassLikeModel : TopLevelModel, MemberModel {
    val members: List<MemberModel>
    val companionObject: CompanionObjectModel?
}