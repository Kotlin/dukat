package org.jetbrains.dukat.astModel

interface ClassLikeModel : TopLevelModel, MemberModel {
    val typeParameters: List<TypeParameterModel>
    val members: List<MemberModel>
    val companionObject: ObjectModel?
    val parentEntities: List<HeritageModel>
    val annotations: List<AnnotationModel>
    val external: Boolean
}