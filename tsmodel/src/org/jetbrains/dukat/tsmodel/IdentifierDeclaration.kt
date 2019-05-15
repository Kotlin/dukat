package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity

data class IdentifierDeclaration(
        val value: String
) : ModuleReferenceDeclaration, HeritageSymbolDeclaration, NameEntity