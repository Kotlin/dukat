package org.jetbrains.dukat.idlDeclarations

data class IDLDictionaryDeclaration(
        val name: String,
        val members: List<IDLDictionaryMemberDeclaration>,
        val parents: List<IDLTypeDeclaration>
) : IDLClassLikeDeclaration