package org.jetbrains.dukat.compiler

import java.io.File

fun fileContent(path: String): String {
    return File(path).bufferedReader().use { it.readText() }
}