package org.jetbrains.dukat.idlDeclarations

data class IDLOperationDeclaration(
        val name: String,
        val returnType: IDLTypeDeclaration,
        val arguments: List<IDLArgumentDeclaration>
) : IDLMemberDeclaration