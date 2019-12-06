package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class ClassModel(
        override val name: NameEntity,
        override var members: List<MemberModel>,
        override val companionObject: ObjectModel?,
        override val typeParameters: List<TypeParameterModel>,
        override val parentEntities: List<HeritageModel>,
        val primaryConstructor: ConstructorModel?,
        override val annotations: MutableList<AnnotationModel>,
        override val comment: CommentEntity?,
        override val external: Boolean,
        val abstract: Boolean,
        override val visibilityModifier: VisibilityModifierModel
) : ClassLikeModel, DelegationModel, MemberModel
