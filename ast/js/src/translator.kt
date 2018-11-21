package org.jetbrains.dukat.ast

external fun require(module: String): dynamic

actual fun translator(astTree: AstTree, fileResolver: FileResolver) {
    val converter = require(path.resolve("./ts/build/ts/converter"));
    val astFactory = AstFactory()
    converter(astTree, astFactory, fileResolver)
}

fun main() {
    val astTree = AstTree(DocumentRoot())
    translator(astTree, FileResolver())

    compile(astTree)
}

