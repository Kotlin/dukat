package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class VariableModel(
        override var name: NameEntity,
        val type: TypeModel,

        val annotations: MutableList<AnnotationModel>,

        var immutable: Boolean,
        val inline: Boolean,
        val external: Boolean,
        val initializer: StatementModel?,
        val get: StatementModel?,
        val set: StatementModel?,
        val typeParameters: List<TypeParameterModel>,
        val extend: ClassLikeReferenceModel?,
        override val visibilityModifier: VisibilityModifierModel,
        override val comment: CommentEntity?,

        override val metaDescription: String? = null
) : TopLevelModel, StatementModel
