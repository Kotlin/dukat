package org.jetbrains.dukat.tsmodel

data class PropertyAccessDeclaration(
        val name: IdentifierDeclaration,
        val expression: HeritageSymbolDeclaration
) : HeritageSymbolDeclaration