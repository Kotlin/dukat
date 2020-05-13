package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class ImportModel(
    override val name: NameEntity,
    val asAlias: IdentifierEntity? = null,
    override val comment: CommentEntity? = null
) : TopLevelModel {
    override val visibilityModifier: VisibilityModifierModel
        get() = VisibilityModifierModel.PUBLIC
}