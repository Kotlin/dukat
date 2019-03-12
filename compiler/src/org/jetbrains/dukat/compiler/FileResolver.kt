package org.jetbrains.dukat.compiler

import java.io.File

typealias ContentResolver = (fileName: String) -> String

class FileResolver {

    fun fileContent(path: String): String {
        return File(path).bufferedReader().use { it.readText() }
    }

    fun resolve(fileName: String): String {
        return fileContent(fileName)
    }
}