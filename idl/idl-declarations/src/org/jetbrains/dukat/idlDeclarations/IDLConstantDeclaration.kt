package org.jetbrains.dukat.idlDeclarations

data class IDLConstantDeclaration(
        val name: String,
        val type: IDLTypeDeclaration
) : IDLMemberDeclaration