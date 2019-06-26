package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class AnnotationModel(
        val name: String,
        val params: List<NameEntity>
) : Entity