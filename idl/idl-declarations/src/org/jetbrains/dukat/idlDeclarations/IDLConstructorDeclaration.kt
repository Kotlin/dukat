package org.jetbrains.dukat.idlDeclarations

data class IDLConstructorDeclaration(
        val arguments: List<IDLArgumentDeclaration>
) : IDLMemberDeclaration