package org.jetbrains.dukat.idlDeclarations

data class IDLNamespaceDeclaration(
        val name: String,
        val attributes: List<IDLAttributeDeclaration>,
        val operations: List<IDLOperationDeclaration>,
        val partial: Boolean
) : IDLTopLevelDeclaration