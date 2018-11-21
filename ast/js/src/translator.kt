package org.jetbrains.dukat.ast

external fun require(module: String): dynamic

actual fun translator(astTree: AstTree) {
    val converter = require(path.resolve("./ts/build/ts/converter"));
    val astFactory = AstFactory()
    converter(astTree, astFactory, FileResolver())
}

fun main() {
    val astTree = AstTree(DocumentRoot())
    translator(astTree)

    compile(astTree)
}

