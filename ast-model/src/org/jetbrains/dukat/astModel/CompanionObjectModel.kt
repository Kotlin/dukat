package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.MemberEntity

data class CompanionObjectModel(
        val name: String,
        val members: List<MemberModel>,

        val parentEntities: List<HeritageModel>
) : MemberEntity

fun CompanionObjectModel?.mergeWith(otherModel: CompanionObjectModel?): CompanionObjectModel? {
    if (otherModel == null) {
        return this
    }
    if (this == null) {
        return CompanionObjectModel(
                "",
                otherModel.members,
                listOf()
        )
    }

    return copy(members = members + otherModel.members)
}