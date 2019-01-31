package org.jetbrains.dukat.tsmodel

data class ModifierDeclaration(val token: String) : Declaration {
    companion object {
        val DECLARE_KEYWORD = ModifierDeclaration("DECLARE")
        val STATIC_KEYWORD = ModifierDeclaration("STATIC")
    }
}