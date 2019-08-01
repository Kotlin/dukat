package org.jetbrains.dukat.idlDeclarations

data class IDLSetterDeclaration(
        val name: String,
        val key: IDLArgumentDeclaration,
        val value: IDLArgumentDeclaration
) : IDLMemberDeclaration