package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelDeclaration


data class EnumDeclaration(
    val values: List<EnumTokenDeclaration>
) : TopLevelDeclaration