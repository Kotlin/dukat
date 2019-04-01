package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class QualifiedNode(
    val left: NameNode,
    val right: IdentifierNode,

    override var nullable: Boolean = false,
    override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration, ModuleReferenceDeclaration, ValueTypeNodeValue, NameNode, TypeNode


fun IdentifierNode.appendLeft(qualifiedLeftNode: NameNode): QualifiedNode {
    return when(qualifiedLeftNode) {
        is IdentifierNode -> this.appendLeft(qualifiedLeftNode)
        is QualifiedNode -> this.appendLeft(qualifiedLeftNode)
        else -> throw Exception("unknown NameNode ${qualifiedLeftNode}")
    }
}

fun IdentifierNode.appendLeft(identifierNode: IdentifierNode): QualifiedNode {
    return QualifiedNode(this, identifierNode)
}

fun IdentifierNode.appendLeft(qualifiedNode: QualifiedNode): QualifiedNode {
    val left = when (qualifiedNode.left) {
        is IdentifierNode -> QualifiedNode(this, qualifiedNode.left)
        is QualifiedNode -> QualifiedNode(when (qualifiedNode.left.left) {
            is IdentifierNode -> this.appendLeft(qualifiedNode.left.left)
            is QualifiedNode -> this.appendLeft(qualifiedNode.left.left)
            else ->throw Exception("unkown qualifiedNode ${qualifiedNode.left.left}")
        }, qualifiedNode.left.right)
        else -> throw Exception("unkown qualifiedNode ${qualifiedNode.left}")
    }
    return QualifiedNode(left, qualifiedNode.right)
}

fun IdentifierNode.appendRight(qualifiedLeftNode: NameNode): QualifiedNode {
    return when(qualifiedLeftNode) {
        is IdentifierNode -> this.appendRight(qualifiedLeftNode)
        is QualifiedNode -> this.appendRight(qualifiedLeftNode)
        else -> throw Exception("unknown NameNode ${qualifiedLeftNode}")
    }
}

fun IdentifierNode.appendRight(qualifiedNode: IdentifierNode): QualifiedNode {
    return QualifiedNode(qualifiedNode, this)
}

fun IdentifierNode.appendRight(qualifiedNode: QualifiedNode): QualifiedNode {
    return QualifiedNode(qualifiedNode, this)
}

fun QualifiedNode.appendRight(identifierNode: IdentifierNode): QualifiedNode {
    return QualifiedNode(this, identifierNode)
}

fun QualifiedNode.appendRight(qualifiedNode: QualifiedNode): QualifiedNode {
    return when(qualifiedNode.left) {
        is IdentifierNode -> appendRight(qualifiedNode.left).appendRight(qualifiedNode.right)
        is QualifiedNode -> appendRight(qualifiedNode.left).appendRight(qualifiedNode.right)
        else -> throw Exception("unknown QualifiedNode")
    }
}

fun NameNode.appendRight(qualifiedNode: NameNode): NameNode {
    return when(this) {
        is IdentifierNode -> when(qualifiedNode) {
            is IdentifierNode -> appendRight(qualifiedNode)
            is QualifiedNode -> appendRight(qualifiedNode)
            else -> throw Exception("unknown QualifiedNode")
        }
        is QualifiedNode -> when(qualifiedNode) {
            is IdentifierNode -> appendRight(qualifiedNode)
            is QualifiedNode -> appendRight(qualifiedNode)
            else -> throw Exception("unknown QualifiedNode")
        }
        else -> throw Exception("unknown QualifiedNode")
    }
}


fun NameNode.shiftRight(): NameNode? {
    return when(this) {
        is IdentifierNode -> null
        is QualifiedNode -> left
        else -> throw Exception("unknown NameNode")
    }
}

fun NameNode.shiftLeft(): NameNode? {
    return when(this) {
        is IdentifierNode -> null
        is QualifiedNode -> {
            val leftShifted = left.shiftLeft()
            if (leftShifted == null) {
                right
            } else {
                QualifiedNode(leftShifted, right)
            }
        }
        else -> throw Exception("unknown NameNode")
    }
}


fun IdentifierNode.debugTranslate(): String = value
fun QualifiedNode.debugTranslate(): String {
    val leftTranslate = when (left) {
        is IdentifierNode -> left.value
        is QualifiedNode -> left.debugTranslate()
        is GenericIdentifierNode -> left.translate()
        else -> throw Exception("unknown QualifiedNode ${left::class.simpleName}")
    }

    return "${leftTranslate}.${right.value}"
}