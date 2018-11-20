package org.jetbrains.dukat.ast

actual class FileResolver {

    @JsName("resolve")
    actual fun resolve(fileName: String): String {
        return "let x = 6;";
    }
}

