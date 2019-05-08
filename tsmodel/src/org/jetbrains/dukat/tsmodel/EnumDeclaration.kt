package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstTopLevelEntity


data class EnumDeclaration(
    val name: String,
    val values: List<EnumTokenDeclaration>
) : AstTopLevelEntity