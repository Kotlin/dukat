package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.AstTopLevelEntity
import org.jetrbains.dukat.nodeLowering.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private fun escapeIdentificator(identificator: String): String {
    val reservedWords = setOf(
            "as",
            "fun",
            "in",
            "interface",
            "is",
            "object",
            "package",
            "typealias",
            "typeof",
            "val",
            "var",
            "when"
    )

    val isReservedWord = reservedWords.contains(identificator)
    val containsDollarSign = identificator.contains("$")
    val containsOnlyUnderscores = "^_+$".toRegex().containsMatchIn(identificator)
    val isEscapedAlready = "^`.*`$".toRegex().containsMatchIn(identificator)

    return if (!isEscapedAlready && (isReservedWord || containsDollarSign || containsOnlyUnderscores)) {
        "`${identificator}`"
    } else {
        identificator
    }
}


private class EscapeIdentificators : ParameterValueLowering {

    private fun IdentifierNode.escape(): IdentifierNode {
        return IdentifierNode(
                value.split(".").joinToString(".") { lowerIdentificator(it) }
        )
    }

    private fun TypeValueNode.escape(): TypeValueNode {
        val typeNodeValue = value
        return when (typeNodeValue) {
            is IdentifierNode -> copy(value = typeNodeValue.escape())
            else -> this
        }
    }

    private fun QualifiedNode.escape(): QualifiedNode {
        val nodeLeft = left
        return when(nodeLeft) {
            is IdentifierNode -> QualifiedNode(nodeLeft.escape(), right.escape(), nullable = nullable)
            is QualifiedNode -> nodeLeft.copy(left = nodeLeft.escape(), right = right.escape())
            else -> throw Exception("unknown QualifiedLeftNode ${nodeLeft}")
        }
    }

    private fun NameNode.escape(): NameNode {
        return when(this) {
            is IdentifierNode -> escape()
            is QualifiedNode -> escape()
            else -> throw Exception("unknown NameNode ${this}")
        }
    }

    override fun lowerIdentificator(identificator: String): String {
        return escapeIdentificator(identificator)
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeValueNode -> declaration.escape()
            is QualifiedNode -> declaration.escape()
            else -> {
                super.lowerParameterValue(declaration)
            }
        }
    }

    override fun lowerTopLevelEntity(declaration: AstTopLevelEntity): AstTopLevelEntity {
        return when (declaration) {
            is EnumNode -> declaration.copy(values = declaration.values.map { value -> value.copy(value = escapeIdentificator(value.value)) })
            else -> super.lowerTopLevelEntity(declaration)
        }
    }

    override fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        return documentRoot.copy(
                fullPackageName = documentRoot.fullPackageName.escape(),
                declarations = lowerTopLevelDeclarations(documentRoot.declarations)
        )
    }
}

fun DocumentRootNode.escapeIdentificators(): DocumentRootNode {
    return EscapeIdentificators().lowerDocumentRoot(this)
}

fun SourceSetNode.escapeIdentificators() = transform { it.escapeIdentificators() }