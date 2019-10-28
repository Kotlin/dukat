package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class InterfaceModel(
        override val name: NameEntity,
        override val members: List<MemberModel>,
        override val companionObject: ObjectModel?,
        val typeParameters: List<TypeParameterModel>,
        override val parentEntities: List<HeritageModel>,
        overrideval annotations: MutableList<AnnotationModel>,
        override val comment: CommentEntity?,
        val external: Boolean,
        override val visibilityModifier: VisibilityModifierModel
) : ClassLikeModel
