package org.jetbrains.dukat.idlDeclarations

data class IDLAttributeDeclaration(
        val name: String,
        val type: IDLTypeDeclaration
) : IDLMemberDeclaration