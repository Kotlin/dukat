package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class InterfaceModel(
        override val name: NameEntity,
        override val members: List<MemberModel>,
        override val companionObject: ObjectModel?,
        override val typeParameters: List<TypeParameterModel>,
        override val parentEntities: List<HeritageModel>,
        override val annotations: MutableList<AnnotationModel>,
        override val comment: CommentEntity?,
        override val external: Boolean,
        override val visibilityModifier: VisibilityModifierModel
) : ClassLikeModel
