package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.ast.model.nodes.statements.StatementNode
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableNode(
        var name: NameEntity,
        val type: ParameterValueDeclaration,

        var exportQualifier: ExportQualifier?,

        var immutable: Boolean,
        val inline: Boolean,
        val typeParameters: List<TypeValueNode>,
        override val uid: String
) : TopLevelEntity, UniqueNode
