package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class HeritageClauseDeclaration(
        val name: HeritageSymbolDeclaration,
        val typeArguments: List<NameEntity>,
        val extending: Boolean
) : Entity