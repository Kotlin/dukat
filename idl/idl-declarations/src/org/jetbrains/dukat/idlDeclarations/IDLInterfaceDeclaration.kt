package org.jetbrains.dukat.idlDeclarations

data class IDLInterfaceDeclaration(
        val name: String,
        val attributes: List<IDLAttributeDeclaration>,
        val operations: List<IDLOperationDeclaration>,
        val parents: List<IDLTypeDeclaration>,
        val extendedAttributes: List<IDLExtendedAttributeDeclaration>
) : IDLTopLevelDeclaration
