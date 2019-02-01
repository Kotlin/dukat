package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration

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

    override fun lowerVariableNode(declaration: VariableNode): VariableNode {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }

    override fun lowerFunctionNode(declaration: FunctionNode): FunctionNode {
        return declaration.copy(name = escapeIdentificator(declaration.name))
    }
}

fun DocumentRootDeclaration.escapeIdentificators(): DocumentRootDeclaration {
    return EscapeIdentificators().lowerDocumentRoot(this)
}