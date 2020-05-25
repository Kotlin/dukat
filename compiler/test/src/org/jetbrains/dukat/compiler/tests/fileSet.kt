package org.jetbrains.dukat.compiler.tests

import java.io.File

fun fileSet(path: String, accept: (String) -> Boolean): Sequence<File> {
    return File(path).walkTopDown().filter { file ->
        accept(file.path)
    }
}
