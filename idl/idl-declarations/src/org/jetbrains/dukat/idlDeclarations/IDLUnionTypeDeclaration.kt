package org.jetbrains.dukat.idlDeclarations

data class IDLUnionTypeDeclaration(
        override val name: String,
        val unionMembers: List<IDLTypeDeclaration>,
        override val nullable: Boolean
) : IDLTypeDeclaration