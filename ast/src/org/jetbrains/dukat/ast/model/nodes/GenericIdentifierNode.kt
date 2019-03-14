package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class GenericIdentifierNode(
        val value: String,
        val typeParameters: List<TypeParameterDeclaration>
) : TypeNodeValue, HeritageSymbolNode, ModuleReferenceDeclaration, QualifiedLeftNode, QualifiedStatementLeftNode, QualifiedStatementRightNode, NameNode, StatementNode