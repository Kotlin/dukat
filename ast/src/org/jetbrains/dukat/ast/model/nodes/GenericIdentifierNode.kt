package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration

data class GenericIdentifierNode(
        val value: String,
        val typeParameters: List<TypeValueNode>
) : HeritageSymbolNode, ModuleReferenceDeclaration, QualifiedStatementLeftNode, QualifiedStatementRightNode, NameNode, StatementNode