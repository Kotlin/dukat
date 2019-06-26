package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.StatementNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity

data class VariableModel(
        override var name: NameEntity,
        val type: TypeModel,

        val annotations: MutableList<AnnotationNode>,

        var immutable: Boolean,
        val inline: Boolean,
        val initializer: StatementNode?,
        val get: StatementNode?,
        val set: StatementNode?,
        val typeParameters: List<TypeParameterModel>
) : MergeableModel, TopLevelNode
