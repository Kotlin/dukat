package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration

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

private class EscapeIdentificators : ParameterValueLowering {
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }
}

fun DocumentRootDeclaration.escapeIdentificators(): DocumentRootDeclaration {
    return EscapeIdentificators().lowerDocumentRoot(this)
}