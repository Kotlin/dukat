package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class GenericIdentifierNode(
        val value: String,
        val typeParameters: List<TypeValueNode>
) : NameEntity, StatementNode