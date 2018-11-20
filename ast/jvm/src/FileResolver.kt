package org.jetbrains.dukat.ast

actual class FileResolver {
    actual fun resolve(fileName: String): String {
        return "let x = 6;";
    }
}


