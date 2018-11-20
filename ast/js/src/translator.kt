package org.jetbrains.dukat.ast

external fun require(module: String): dynamic

actual fun translator(fileResolver: FileResolver) {
    val converter = require(path.resolve("./ts/build/ts/converter"));
    converter(fileResolver)
}

fun main() {
    translator(FileResolver())
}

