package org.jetbrains.dukat.tsmodel

interface WithModifiersDeclaration {
    val modifiers: List<ModifierDeclaration>

    fun hasDefaultModifier(): Boolean {
        return modifiers.contains(ModifierDeclaration.DEFAULT_KEYWORD)
    }

    fun hasExportModifier(): Boolean {
        return modifiers.contains(ModifierDeclaration.EXPORT_KEYWORD)
    }

    fun hasDeclareModifier(): Boolean {
        return modifiers.contains(ModifierDeclaration.DECLARE_KEYWORD)
    }
}

