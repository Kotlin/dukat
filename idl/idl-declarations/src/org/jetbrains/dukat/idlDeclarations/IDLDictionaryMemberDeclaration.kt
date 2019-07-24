package org.jetbrains.dukat.idlDeclarations

data class IDLDictionaryMemberDeclaration(
        val name: String,
        val type: IDLTypeDeclaration,
        val defaultValue: String?
) : IDLMemberDeclaration