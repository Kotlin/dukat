package org.jetbrains.dukat.compiler

typealias ContentResolver = (fileName: String) -> String

class FileResolver {

    fun resolve(fileName: String): String {
        return fileContent(fileName)
    }
}