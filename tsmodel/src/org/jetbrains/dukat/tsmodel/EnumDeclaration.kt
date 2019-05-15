package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelEntity


data class EnumDeclaration(
    val name: String,
    val values: List<EnumTokenDeclaration>
) : TopLevelEntity