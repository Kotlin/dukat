package org.jetbrains.dukat.idlDeclarations

data class IDLInterfaceDeclaration(
        val name: String,
        val attributes: List<IDLAttributeDeclaration>,
        val constants: List<IDLConstantDeclaration>,
        val operations: List<IDLOperationDeclaration>,
        val primaryConstructor: IDLConstructorDeclaration?,
        val constructors: List<IDLConstructorDeclaration>,
        val parents: List<IDLSingleTypeDeclaration>,
        val extendedAttributes: List<IDLExtendedAttributeDeclaration>,
        val generated: Boolean = false
) : IDLTopLevelDeclaration
