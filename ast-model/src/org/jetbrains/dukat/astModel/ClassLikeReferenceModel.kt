package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class ClassLikeReferenceModel(
        val name: NameEntity,
        val typeParameters: List<NameEntity>
) : Entity