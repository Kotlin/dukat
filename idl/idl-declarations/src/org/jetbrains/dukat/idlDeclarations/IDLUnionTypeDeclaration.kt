package org.jetbrains.dukat.idlDeclarations

data class IDLUnionTypeDeclaration(
        override val name: String,
        val unionMembers: List<IDLTypeDeclaration>,
        val originFile: String?,
        override val nullable: Boolean,
        override val comment: String? = null
) : IDLTypeDeclaration