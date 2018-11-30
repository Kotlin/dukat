package org.jetbrains.dukat.ast

expect fun createTranslator(): (fileName: String) -> AstTree;