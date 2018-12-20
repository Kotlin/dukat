package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.DocumentRoot

expect fun createTranslator(): (fileName: String) -> DocumentRoot;