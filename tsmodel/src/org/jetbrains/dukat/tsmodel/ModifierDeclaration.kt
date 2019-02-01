package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Declaration

data class ModifierDeclaration(val token: String) : Declaration {
    companion object {
        val DECLARE_KEYWORD = ModifierDeclaration("DECLARE")
        val STATIC_KEYWORD = ModifierDeclaration("STATIC")
        val EXPORT_KEYWORD = ModifierDeclaration("EXPORT")
    }
}