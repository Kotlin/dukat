package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class InterfaceModel(
    override val name: NameEntity,
    override val members: List<MemberModel>,
    override val companionObject: ObjectModel?,
    val typeParameters: List<TypeParameterModel>,
    val parentEntities: List<HeritageModel>,
    val annotations: MutableList<AnnotationModel>,
    val comment: CommentModel?,
    val external: Boolean,
    override val visibilityModifier: VisibilityModifierModel
) : ClassLikeModel
