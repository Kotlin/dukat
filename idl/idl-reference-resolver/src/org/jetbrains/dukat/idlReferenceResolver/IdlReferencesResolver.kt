package org.jetbrains.dukat.idlReferenceResolver

interface IdlReferencesResolver {
    fun resolveReferences(fileName: String): List<String>
}