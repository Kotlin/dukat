package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity

data class ModifierDeclaration(val token: String) : Entity {
    companion object {
        val DECLARE_KEYWORD = ModifierDeclaration("DECLARE")
        val STATIC_KEYWORD = ModifierDeclaration("STATIC")
        val EXPORT_KEYWORD = ModifierDeclaration("EXPORT")
        val DEFAULT_KEYWORD = ModifierDeclaration("DEFAULT")
    }
}