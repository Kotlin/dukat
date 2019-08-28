package org.jetbrains.dukat.idlDeclarations

data class IDLEnumDeclaration(
        val name: String,
        val members: List<String>,
        val partial: Boolean = false
) : IDLTopLevelDeclaration

private fun String.addUnderscoreIfStartsWithNumber(): String {
    return if (matches(Regex("^[0-9].*$"))) {
        "_$this"
    } else {
        this
    }
}

fun processEnumMember(memberName: String): String {
    return memberName
            .removeSurrounding("\"")
            .toUpperCase()
            .replace('-', '_')
            .replace('/', '_')
            .replace('+', '_')
            .addUnderscoreIfStartsWithNumber()
            .ifEmpty { "EMPTY" }
}