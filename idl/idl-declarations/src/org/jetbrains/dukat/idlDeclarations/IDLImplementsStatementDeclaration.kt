package org.jetbrains.dukat.idlDeclarations

data class IDLImplementsStatementDeclaration(
        val child: IDLTypeDeclaration,
        val parent: IDLTypeDeclaration
) : IDLTopLevelDeclaration