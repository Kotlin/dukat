package org.jetbrains.dukat.idlDeclarations

data class IDLExtendedAttributeDeclaration(
        val attributeName: String?,
        val functionName: String?,
        val functionArguments: List<IDLArgumentDeclaration>
) : IDLDeclaration