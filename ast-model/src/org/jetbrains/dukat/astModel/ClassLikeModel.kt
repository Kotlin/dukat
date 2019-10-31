package org.jetbrains.dukat.astModel

interface ClassLikeModel : TopLevelModel, MemberModel {
    val members: List<MemberModel>
    val companionObject: ObjectModel?
    val parentEntities: List<HeritageModel>
    val annotations: List<AnnotationModel>
}