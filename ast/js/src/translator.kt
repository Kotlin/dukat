package org.jetbrains.dukat.ast

external fun require(module: String): dynamic

actual fun createTranslator(): (fileName: String) -> AstTree {
    val converter = require(path.resolve("./ts/build/ts/converter"));
    val astFactory = AstFactory()
    return {fileName -> converter(astFactory, FileResolver(), fileName)}
}

fun main() {
    val astTree = createTranslator()("./ast/common/test/data/simplest_var.declarations.d.ts")

    println(compile(astTree))
}

