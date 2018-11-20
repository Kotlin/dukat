package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.nodejs.EncodingOptions
import org.jetbrains.dukat.ast.nodejs.fs

actual fun fileContent(path: String): String {
    return fs.readFileSync(path, EncodingOptions());
}

