package org.jetbrains.dukat.idlDeclarations

data class IDLEnumDeclaration(
        override val name: String,
        val members: List<String>,
        val unions: List<IDLSingleTypeDeclaration>,
        val partial: Boolean = false
) : IDLClassLikeDeclaration

fun processEnumMember(memberName: String): String {
    return memberName
            .removeSurrounding("\"")
            .toUpperCase()
            .replace('-', '_')
            .ifEmpty { "EMPTY" }
}