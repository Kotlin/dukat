package org.jetbrains.dukat.idlReferenceResolver

import org.jetbrains.dukat.translatorString.IDL_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import java.io.File

class DirectoryReferencesResolver : IdlReferencesResolver {

    override fun resolveReferences(fileName: String): List<String> {
        val directory = File(fileName).parentFile
        return directory.listFiles()?.map { it.canonicalFile }?.filter {
            it.isFile &&
                    (it.name.endsWith(WEBIDL_DECLARATION_EXTENSION)
                            || it.name.endsWith(IDL_DECLARATION_EXTENSION)) &&
                    it.absolutePath != File(fileName).canonicalFile.absolutePath
        }?.map { it.absolutePath }.orEmpty()
    }
}
