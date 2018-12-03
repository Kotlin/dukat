package org.jetbrains.dukat.ast

import kotlin.js.JsName

typealias ContentResolver = (fileName: String) -> String

class FileResolver {

    @JsName("resolve")
    fun resolve(fileName: String): String {
        return fileContent(fileName)
    }
}