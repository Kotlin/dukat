package org.jetbrains.dukat.idlDeclarations

data class IDLTypeDeclaration(
        val name: String,
        val typeParameter: IDLTypeDeclaration?,
        val nullable: Boolean
) : IDLDeclaration