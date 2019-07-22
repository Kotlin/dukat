package org.jetbrains.dukat.idlDeclarations

data class IDLSingleTypeDeclaration(
        val name: String,
        val typeParameter: IDLTypeDeclaration?,
        override val nullable: Boolean
) : IDLTypeDeclaration