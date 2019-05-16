package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.MergableNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.StatementNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.TopLevelEntity

data class VariableModel(
        var name: NameNode,
        val type: TypeModel,

        val annotations: MutableList<AnnotationNode>,

        var immutable: Boolean,
        val inline: Boolean,
        val initializer: StatementNode?,
        val get: StatementNode?,
        val set: StatementNode?,
        val typeParameters: List<TypeParameterModel>
) : TopLevelEntity, MergableNode, TopLevelNode
