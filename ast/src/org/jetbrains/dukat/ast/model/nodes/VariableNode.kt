package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableNode(
        var name: NameNode,
        val type: ParameterValueDeclaration,

        val annotations: MutableList<AnnotationNode>,

        var immutable: Boolean,
        val inline: Boolean,
        val get: StatementNode?,
        val set: StatementNode?,
        val typeParameters: List<TypeParameterDeclaration>,
        var owner: DocumentRootNode?,
        val uid: String
) : TopLevelDeclaration, MergableNode
