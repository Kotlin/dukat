package org.jetbrains.dukat.idlDeclarations

data class IDLEnumDeclaration(
        val name: String,
        val members: List<String>,
        val partial: Boolean = false
) : IDLTopLevelDeclaration

fun processEnumMember(memberName: String): String {
    return memberName
            .removeSurrounding("\"")
            .toUpperCase()
            .replace('-', '_')
            .ifEmpty { "EMPTY" }
}