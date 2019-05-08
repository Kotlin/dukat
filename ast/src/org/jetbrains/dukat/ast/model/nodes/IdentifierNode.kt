package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration

data class IdentifierNode(
    val value: String
) : HeritageSymbolNode, ModuleReferenceDeclaration, QualifiedStatementLeftNode, QualifiedStatementRightNode, NameNode, StatementNode