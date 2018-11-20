package org.jetbrains.dukat.ast

external fun require(module: String): dynamic

val path = require("path");
val fs = require("fs");

external val process: dynamic;

actual fun translator(fileResolver: FileResolver) {
    val converter = require(path.resolve("./ts/build/ts/converter"));

    converter(fileResolver)
}

fun main() {
    translator(FileResolver())
}

