package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.marker.TypeModel
import org.jetbrains.dukat.astCommon.AstEntity

data class TypeParameterModel(
        val name: String,
        val constraints: List<TypeModel>
) : AstEntity