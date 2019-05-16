package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering

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


private class EscapeIdentificators : NodeTypeLowering {

    private fun IdentifierNode.escape(): IdentifierNode {
        return IdentifierNode(
                value.split(".").joinToString(".") { lowerIdentificator(it) }
        )
    }

    private fun TypeValueNode.escape(): TypeValueNode {
        val typeNodeValue = value
        return when (typeNodeValue) {
            is IdentifierNode -> copy(value = typeNodeValue.escape())
            is QualifiedNode -> copy(value = typeNodeValue.escape())
            else -> this
        }
    }

    private fun QualifiedNode.escape(): QualifiedNode {
        val nodeLeft = left
        return when(nodeLeft) {
            is IdentifierNode -> QualifiedNode(nodeLeft.escape(), right.escape())
            is QualifiedNode -> nodeLeft.copy(left = nodeLeft.escape(), right = right.escape())
            else -> raiseConcern("unknown QualifiedLeftNode ${nodeLeft}") { this }
        }
    }

    private fun NameEntity.escape(): NameEntity {
        return when(this) {
            is IdentifierNode -> escape()
            is QualifiedNode -> escape()
            else -> raiseConcern("unknown NameNode ${this}") { this }
        }
    }

    override fun lowerIdentificator(identificator: String): String {
        return escapeIdentificator(identificator)
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeValueNode -> declaration.escape()
            else -> {
                super.lowerType(declaration)
            }
        }
    }

    override fun lowerTopLevelEntity(declaration: TopLevelEntity): TopLevelEntity {
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