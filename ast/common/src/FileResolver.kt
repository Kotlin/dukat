package org.jetbrains.dukat.ast

import kotlin.js.JsName

class FileResolver {

    @JsName("resolve")
    fun resolve(fileName: String): String {
        return fileContent(fileName);
    }
}