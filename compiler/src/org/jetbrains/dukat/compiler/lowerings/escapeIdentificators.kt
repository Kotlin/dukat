package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration

private fun escapeIdentificator(identificator: String): String {
    val reservedWords = setOf(
           "_", "object", "when", "val", "var", "as", "package", "fun", "when", "typealias", "typeof", "in"
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
        return declaration.copy(
            name = escapeIdentificator(declaration.name),
            parameters = declaration.parameters.map {param -> lowerParameterDeclaration(param)}
        )
    }

    override fun lowerMethodNode(declaration: MethodNode): MethodNode {
        return declaration.copy(
            name = escapeIdentificator(declaration.name),
            parameters = declaration.parameters.map {param -> lowerParameterDeclaration(param)}
        )
    }

    override fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is EnumNode -> declaration.copy(values = declaration.values.map {value -> value.copy(value = escapeIdentificator(value.value)) })
            else -> super.lowerTopLevelDeclaration(declaration)
        }
    }
}

fun DocumentRootNode.escapeIdentificators(): DocumentRootNode {
    return EscapeIdentificators().lowerDocumentRoot(this)
}