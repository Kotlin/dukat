package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.MemberEntity

data class CompanionObjectModel(
        val name: String,
        val members: List<MemberModel>,

        val parentEntities: List<HeritageModel>
) : MemberEntity