package org.jetbrains.dukat.idlDeclarations

data class IDLOperationDeclaration(
        val name: String,
        val returnType: IDLTypeDeclaration,
        val arguments: List<IDLArgumentDeclaration>,
        val static: Boolean,
        val override: Boolean
) : IDLMemberDeclaration