package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode


data class MethodModel(
        val name: String,
        val parameters: List<ParameterModel>,
        val type: TypeNode,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: Boolean,
        val operator: Boolean,
        val annotations: List<AnnotationNode>,

        val open: Boolean,
        val definedExternally: Boolean
) : MemberNode, MemberModel
