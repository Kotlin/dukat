package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class ObjectModel(
        override val name: NameEntity,
        override val members: List<MemberModel>,
        override val parentEntities: List<HeritageModel>,
        override val visibilityModifier: VisibilityModifierModel,
        override val comment: CommentEntity?,
        override val external: Boolean,
) : ClassLikeModel, CanHaveExternalModifierModel {
    override val annotations: List<AnnotationModel> = emptyList()
    override val typeParameters: List<TypeParameterModel> = emptyList()
    override val companionObject: ObjectModel? = null
}

fun ObjectModel?.mergeWith(otherModel: ObjectModel?): ObjectModel? {
    if (otherModel == null) {
        return this
    }
    if (this == null) {
        return ObjectModel(
                IdentifierEntity(""),
                otherModel.members,
                listOf(),
                VisibilityModifierModel.DEFAULT,
                null,
                otherModel.external
        )
    }

    return copy(members = members + otherModel.members)
}