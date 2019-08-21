package org.jetbrains.dukat.idlDeclarations

data class IDLDictionaryMemberDeclaration(
        val name: String,
        val type: IDLTypeDeclaration,
        val defaultValue: String?,
        val required: Boolean
) : IDLMemberDeclaration