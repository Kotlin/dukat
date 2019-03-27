package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class GenericIdentifierNode(
        val value: String,
        val typeParameters: List<TypeParameterDeclaration>
) : HeritageSymbolNode, ModuleReferenceDeclaration, QualifiedStatementLeftNode, QualifiedStatementRightNode, NameNode, StatementNode