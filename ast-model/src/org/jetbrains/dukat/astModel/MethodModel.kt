package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.statements.BlockStatementModel

data class MethodModel(
        val name: NameEntity,
        override val parameters: List<ParameterModel>,
        override val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: NameEntity?,
        val operator: Boolean,
        val annotations: List<AnnotationModel>,

        val open: Boolean,

        val body: BlockStatementModel?
) : MemberModel, CallableModel<ParameterModel>
