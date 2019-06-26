package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.StatementNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class FunctionModel(
        override val name: NameEntity,
        val parameters: List<ParameterModel>,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val annotations: MutableList<AnnotationNode>,

        val export: Boolean,
        val inline: Boolean,
        val operator: Boolean,

        val body: List<StatementNode>
) : MemberEntity, MergeableModel, TopLevelNode