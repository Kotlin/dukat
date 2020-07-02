package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class VariableNode(
        var name: NameEntity,
        val type: TypeNode,

        override var exportQualifier: ExportQualifier?,

        var immutable: Boolean,
        val inline: Boolean,
        val typeParameters: List<TypeValueNode>,
        val extend: ClassLikeReferenceNode?,
        override val uid: String,
        val comment: CommentEntity?,
        override val external: Boolean,
        val hasType: Boolean
) : TopLevelNode, ExportableNode
