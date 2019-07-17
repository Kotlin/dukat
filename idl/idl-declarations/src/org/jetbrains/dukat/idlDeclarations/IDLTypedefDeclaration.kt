package org.jetbrains.dukat.idlDeclarations

data class IDLTypedefDeclaration(
        val name: String,
        val typeReference: IDLTypeDeclaration
) : IDLTopLevelDeclaration