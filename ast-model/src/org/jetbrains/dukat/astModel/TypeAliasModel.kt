package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.ExpectActualModifier
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class TypeAliasModel(
        override val name: NameEntity,
        val typeReference: TypeModel,
        val typeParameters: List<TypeParameterModel>,
        override val visibilityModifier: VisibilityModifierModel,
        override val comment: CommentEntity?,

        override val expectActualModifier: ExpectActualModifier? = null
) : TopLevelModel