package org.jetbrains.dukat.idlDeclarations

data class IDLUnionTypeDeclaration(
        val unionMembers: List<IDLTypeDeclaration>,
        override val nullable: Boolean
) : IDLTypeDeclaration