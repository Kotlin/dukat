package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration

data class IdentifierNode(
    val value: String
) : TypeNodeValue, HeritageSymbolNode, ModuleReferenceDeclaration, QualifiedLeftNode