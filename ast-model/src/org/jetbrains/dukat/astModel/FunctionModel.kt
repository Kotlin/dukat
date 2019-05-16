package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.MergableNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.StatementNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

data class FunctionModel(
        val name: NameNode,
        val parameters: List<ParameterModel>,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val generatedReferenceNodes: MutableList<GeneratedInterfaceReferenceNode>,
        val annotations: MutableList<AnnotationNode>,

        val export: Boolean,
        val inline: Boolean,
        val operator: Boolean,

        val body: List<StatementNode>
) : MemberEntity, TopLevelEntity, MergableNode, TopLevelNode