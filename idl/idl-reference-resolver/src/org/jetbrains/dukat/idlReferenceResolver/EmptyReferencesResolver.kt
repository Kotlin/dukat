package org.jetbrains.dukat.idlReferenceResolver

class EmptyReferencesResolver : IdlReferencesResolver {
    override fun resolveReferences(fileName: String): List<String> = listOf()
}