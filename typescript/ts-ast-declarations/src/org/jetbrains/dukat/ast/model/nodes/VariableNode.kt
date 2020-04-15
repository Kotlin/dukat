package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableNode(
        var name: NameEntity,
        val type: ParameterValueDeclaration,

        override var exportQualifier: ExportQualifier?,

        var immutable: Boolean,
        val inline: Boolean,
        val typeParameters: List<TypeValueNode>,
        val extend: ClassLikeReferenceNode?,
        override val uid: String,
        val comment: CommentEntity?,
        override val external: Boolean
) : TopLevelNode, ExportableNode
