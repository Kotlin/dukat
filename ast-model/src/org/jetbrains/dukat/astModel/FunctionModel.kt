package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel

data class FunctionModel(
        override val name: NameEntity,
        override val parameters: List<ParameterModel>,
        override val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val annotations: MutableList<AnnotationModel>,

        val export: Boolean,
        val inline: Boolean,
        val operator: Boolean,

        override val extend: ClassLikeReferenceModel?,
        val body: BlockStatementModel?,
        override val visibilityModifier: VisibilityModifierModel,
        override val comment: CommentEntity?,
        override val external: Boolean
) : MemberEntity, TopLevelModel, CanHaveExternalModifierModel, CallableModel<ParameterModel>, CanBeExtensionModel