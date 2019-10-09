package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifier

data class FunctionModel(
        override val name: NameEntity,
        val parameters: List<ParameterModel>,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val annotations: MutableList<AnnotationModel>,

        val export: Boolean,
        val inline: Boolean,
        val operator: Boolean,

        val extend: ClassLikeReferenceModel?,
        val body: List<StatementModel>,
        override val visibilityModifier: VisibilityModifier
) : MemberEntity, MergeableModel, TopLevelModel