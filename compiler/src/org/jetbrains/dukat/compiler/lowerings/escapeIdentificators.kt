package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private fun escapeIdentificator(identificator: String): String {
    val reservedWords = setOf(
            "object", "when", "val", "var", "as", "package", "fun", "typealias", "typeof", "in", "interface", "is"
    )


    return if (reservedWords.contains(identificator) || identificator.contains("$") || "^_+$".toRegex().containsMatchIn(identificator)) {
        if ("^`.*`$".toRegex().containsMatchIn(identificator)) {
            identificator
        } else {
            "`${identificator}`"
        }
    } else {
        identificator
    }
}


private class EscapeIdentificators : ParameterValueLowering {

    private fun IdentifierNode.escape(): IdentifierNode {
        return IdentifierNode(
                value.split(".").joinToString(".") { lowerStringIdentificator(it) }
        )
    }

    private fun TypeNode.escape(): TypeNode {
        val typeNodeValue = value
        return when (typeNodeValue) {
            is IdentifierNode -> copy(value = typeNodeValue.escape())
            else -> this
        }
    }

    private fun QualifiedNode.escape(): QualifiedNode {
        val nodeLeft = left
        return when(nodeLeft) {
            is IdentifierNode -> QualifiedNode(nodeLeft.escape(), right.escape())
            is QualifiedNode -> QualifiedNode(nodeLeft.escape(), right.escape())
            else -> throw Exception("unknown QualifiedLeftnode")
        }
    }

    override fun lowerStringIdentificator(identificator: String): String {
        return escapeIdentificator(identificator)
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeNode -> declaration.escape()
            is QualifiedNode -> declaration.escape()
            else -> {
                super.lowerParameterValue(declaration)
            }
        }
    }

    override fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is EnumNode -> declaration.copy(values = declaration.values.map { value -> value.copy(value = escapeIdentificator(value.value)) })
            else -> super.lowerTopLevelDeclaration(declaration)
        }
    }

    override fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        return documentRoot.copy(
                fullPackageName = documentRoot.fullPackageName.split(".").joinToString(".") { lowerStringIdentificator(it) },
                declarations = lowerTopLevelDeclarations(documentRoot.declarations)
        )
    }
}

fun DocumentRootNode.escapeIdentificators(): DocumentRootNode {
    return EscapeIdentificators().lowerDocumentRoot(this)
}