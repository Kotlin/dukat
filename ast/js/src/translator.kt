package org.jetbrains.dukat.ast

external fun require(module: String): dynamic

actual fun translator(): AstTree {
    val converter = require(path.resolve("./ts/build/ts/converter"));
    val astFactory = AstFactory()
    return converter(astFactory, FileResolver())
}

fun main() {
    val astTree = translator()

    compile(astTree)
}

