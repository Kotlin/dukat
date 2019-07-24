package org.jetbrains.dukat.idlDeclarations

data class IDLFunctionTypeDeclaration(
        override val name: String,
        val returnType: IDLTypeDeclaration,
        val arguments: List<IDLArgumentDeclaration>,
        override val nullable: Boolean
): IDLTypeDeclaration