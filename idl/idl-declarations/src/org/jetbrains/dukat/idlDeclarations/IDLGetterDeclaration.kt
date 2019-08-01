package org.jetbrains.dukat.idlDeclarations

data class IDLGetterDeclaration(
        val name: String,
        val key: IDLArgumentDeclaration,
        val valueType: IDLTypeDeclaration
) : IDLMemberDeclaration