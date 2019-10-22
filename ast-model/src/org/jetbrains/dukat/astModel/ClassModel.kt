package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class ClassModel(
        override val name: NameEntity,
        override var members: List<MemberModel>,
        override val companionObject: ObjectModel?,
        val typeParameters: List<TypeParameterModel>,
        override val parentEntities: List<HeritageModel>,
        val primaryConstructor: ConstructorModel?,
        val annotations: MutableList<AnnotationModel>,
        val comment: CommentModel?,
        val external: Boolean,
        val abstract: Boolean,
        override val visibilityModifier: VisibilityModifierModel
) : ClassLikeModel, DelegationModel, MemberModel
