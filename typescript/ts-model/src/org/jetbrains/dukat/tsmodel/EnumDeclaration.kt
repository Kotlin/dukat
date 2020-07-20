package org.jetbrains.dukat.tsmodel

data class EnumDeclaration(
        val name: String,
        val values: List<EnumTokenDeclaration>,
        val uid: String,
        val modifiers: Set<ModifierDeclaration>
) : TopLevelDeclaration