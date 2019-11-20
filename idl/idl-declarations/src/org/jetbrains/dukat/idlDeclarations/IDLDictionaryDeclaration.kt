package org.jetbrains.dukat.idlDeclarations

data class IDLDictionaryDeclaration(
        override val name: String,
        val members: List<IDLDictionaryMemberDeclaration>,
        val parents: List<IDLSingleTypeDeclaration>,
        val unions: List<IDLSingleTypeDeclaration>,
        val partial: Boolean
) : IDLClassLikeDeclaration