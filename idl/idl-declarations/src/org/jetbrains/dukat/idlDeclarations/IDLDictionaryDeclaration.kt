package org.jetbrains.dukat.idlDeclarations

data class IDLDictionaryDeclaration(
        val name: String,
        val members: List<IDLDictionaryMemberDeclaration>,
        val parents: List<IDLSingleTypeDeclaration>,
        val partial: Boolean
) : IDLTopLevelDeclaration