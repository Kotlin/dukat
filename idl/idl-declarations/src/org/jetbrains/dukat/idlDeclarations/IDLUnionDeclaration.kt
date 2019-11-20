package org.jetbrains.dukat.idlDeclarations

data class IDLUnionDeclaration(
        override val name: String,
        val unions: List<IDLSingleTypeDeclaration>
) : IDLClassLikeDeclaration