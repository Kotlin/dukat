package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Declaration

data class ModifierDeclaration(val token: String) : Declaration {
    companion object {
        val DECLARE_KEYWORD = ModifierDeclaration("DECLARE")
        val STATIC_KEYWORD = ModifierDeclaration("STATIC")
        val EXPORT_KEYWORD = ModifierDeclaration("EXPORT")
        val DEFAULT_KEYWORD = ModifierDeclaration("DEFAULT")

        fun hasDefault(modifiers: List<ModifierDeclaration>) = modifiers.contains(DEFAULT_KEYWORD)
        fun hasExport(modifiers: List<ModifierDeclaration>) = modifiers.contains(EXPORT_KEYWORD)
    }
}