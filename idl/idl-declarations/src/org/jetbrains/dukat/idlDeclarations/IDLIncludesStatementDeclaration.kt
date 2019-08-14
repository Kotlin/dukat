package org.jetbrains.dukat.idlDeclarations

data class IDLIncludesStatementDeclaration(
        val child: IDLTypeDeclaration,
        val parent: IDLTypeDeclaration
) : IDLTopLevelDeclaration