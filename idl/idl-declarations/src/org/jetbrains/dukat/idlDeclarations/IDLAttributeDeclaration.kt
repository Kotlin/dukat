package org.jetbrains.dukat.idlDeclarations

data class IDLAttributeDeclaration(
        val name: String,
        val type: IDLTypeDeclaration,
        val static: Boolean,
        val readOnly: Boolean,
        val override: Boolean,
        val open: Boolean = false
) : IDLMemberDeclaration