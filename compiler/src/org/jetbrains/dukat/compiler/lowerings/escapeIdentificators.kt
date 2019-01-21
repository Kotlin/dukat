package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.ParameterDeclaration

private fun escapeIdentificator(identificator: String): String {
    val reservedWords = setOf("object")
    return if (reservedWords.contains(identificator)) {
        "`${identificator}`"
    } else {
        identificator
    }
}

private class EscapeIdentificators : ParameterValueLowering() {
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }
}

fun DocumentRoot.escapeIdentificators(): DocumentRoot {
    return EscapeIdentificators().lowerDocumentRoot(this)
}