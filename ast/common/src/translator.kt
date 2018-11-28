package org.jetbrains.dukat.ast

expect fun translator(fileName: String): AstTree

expect fun createTranslator(): (fileName: String) -> AstTree;