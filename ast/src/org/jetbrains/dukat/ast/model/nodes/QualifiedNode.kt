package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class QualifiedNode(
    val left: NameNode,
    val right: IdentifierNode,

    override var nullable: Boolean = false,
    override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration, ModuleReferenceDeclaration, TypeNodeValue, NameNode


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

fun QualifiedNode.shiftRight(): NameNode = left


fun NameNode.shiftRight(): NameNode? {
    return when(this) {
        is IdentifierNode -> null
        is QualifiedNode -> left
        else -> throw Exception("unknown NameNode")
    }
}


fun IdentifierNode.debugTranslate(): String = value
fun NameNode.debugTranslate(): String {
    return when(this) {
        is IdentifierNode -> debugTranslate()
        is QualifiedNode -> debugTranslate()
        is GenericIdentifierNode -> value + "<${typeParameters.joinToString(", ") { typeParameter -> typeParameter.name }}>"
        else -> throw Exception("unknown NameNode")
    }
}

fun QualifiedNode.debugTranslate(): String {
    val leftTranslate = when (left) {
        is IdentifierNode -> left.value
        is QualifiedNode -> left.debugTranslate()
        else -> throw Exception("XXX")
    }

    return "${leftTranslate}.${right.value}"
}

fun main() {
    val abQualifiedNode = QualifiedNode(IdentifierNode("a"), IdentifierNode("b"))
    println(abQualifiedNode.debugTranslate())

    val qualifiedNode = IdentifierNode("d").appendLeft(QualifiedNode(abQualifiedNode, IdentifierNode("c")))
    println(qualifiedNode.debugTranslate())

    println(qualifiedNode.shiftRight().debugTranslate())
    println(qualifiedNode.appendRight(IdentifierNode("x")).debugTranslate())
    println(qualifiedNode.appendRight(
                    QualifiedNode(QualifiedNode(IdentifierNode("x"), IdentifierNode("y")), IdentifierNode("z"))
    ).debugTranslate())
}