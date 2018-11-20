package org.jetbrains.dukat.ast

import java.io.File

actual fun fileContent(path: String): String {
    return File(path).bufferedReader().use { it.readText() }
}