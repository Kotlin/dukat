package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration

private fun escapeIdentificator(identificator: String): String {
    val reservedWords = setOf(
            "object", "when", "val", "var", "as", "package", "fun", "when", "typealias", "typeof", "in"
    )
    return if (reservedWords.contains(identificator) || identificator.contains("$")) {
        "`${identificator}`"
    } else {
        identificator
    }
}

private class EscapeIdentificators : ParameterValueLowering() {
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }

    override fun lowerMethodDeclaration(declaration: MethodDeclaration): MethodDeclaration {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }
}

fun DocumentRoot.escapeIdentificators(): DocumentRoot {
    return EscapeIdentificators().lowerDocumentRoot(this)
}