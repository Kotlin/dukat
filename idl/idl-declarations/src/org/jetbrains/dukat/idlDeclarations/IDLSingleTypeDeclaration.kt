package org.jetbrains.dukat.idlDeclarations

data class IDLSingleTypeDeclaration(
        override val name: String,
        val typeParameter: IDLTypeDeclaration?,
        override val nullable: Boolean
) : IDLTypeDeclaration