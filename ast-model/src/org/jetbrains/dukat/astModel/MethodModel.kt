package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.astCommon.NameEntity


data class MethodModel(
        val name: NameEntity,
        val parameters: List<ParameterModel>,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: Boolean,
        val operator: Boolean,
        val annotations: List<AnnotationNode>,

        val open: Boolean
) : MemberModel
