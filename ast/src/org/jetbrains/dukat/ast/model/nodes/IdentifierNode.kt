package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class IdentifierNode(
        val value: String
) : NameEntity, StatementNode