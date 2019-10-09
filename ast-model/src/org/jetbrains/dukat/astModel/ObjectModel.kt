package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifier

data class ObjectModel(
        override val name: NameEntity,
        val members: List<MemberModel>,

        val parentEntities: List<HeritageModel>,
        override val visibilityModifier: VisibilityModifier
) : MemberEntity, TopLevelModel

fun ObjectModel?.mergeWith(otherModel: ObjectModel?): ObjectModel? {
    if (otherModel == null) {
        return this
    }
    if (this == null) {
        return ObjectModel(
                IdentifierEntity(""),
                otherModel.members,
                listOf(),
                VisibilityModifier.DEFAULT
        )
    }

    return copy(members = members + otherModel.members)
}