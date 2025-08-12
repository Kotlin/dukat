package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.ExpectActualModifier
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class EnumModel(
        override val name: NameEntity,
        val values: List<EnumTokenModel>,
        override val visibilityModifier: VisibilityModifierModel,
        override val comment: CommentEntity?,
        override val external: Boolean = true,
        override val expectActualModifier: ExpectActualModifier? = null
) : CanHaveExternalModifierModel, CanBeExpectActual, MemberModel