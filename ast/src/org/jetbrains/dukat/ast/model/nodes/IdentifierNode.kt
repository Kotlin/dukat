package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration

data class IdentifierNode(
    val value: String
) : ModuleReferenceDeclaration, NameNode, StatementNode