package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration

interface NameNode

fun NameNode.translate(): String = when (this) {
    is IdentifierNode -> value
    is QualifiedNode -> {
        "${left.translate()}.${right.translate()}"
    }
    is GenericIdentifierNode -> value + "<${typeParameters.joinToString(", ") { typeParameter -> typeParameter.value.translate() }}>"
    else -> raiseConcern("unknown NameNode ${this}") { this.toString() }
}

fun NameEntity.toNode(): NameNode {
    return when (this) {
        is IdentifierDeclaration -> IdentifierNode(value)
        is QualifiedNamedDeclaration -> QualifiedNode(left = left.toNode(), right = IdentifierNode(right.value))
        else -> raiseConcern("unknown QualifiedLeftDeclaration") { IdentifierNode(this.toString()) }
    }
}


private fun NameNode.countDepth(current: Int): Int {
    return when (this) {
        is IdentifierNode -> current + 1
        is QualifiedNode -> left.countDepth(current) + right.countDepth(current)
        else -> raiseConcern("unknown NameNode ${this}") { 0 }
    }
}

fun NameNode.process(handler: (String) -> String): NameNode {
    return when (this) {
        is IdentifierNode -> IdentifierNode(handler(value))
        is QualifiedNode -> copy(left = left.process(handler), right = right.process(handler) as IdentifierNode)
        is GenericIdentifierNode -> copy(value = handler(value))
        else -> raiseConcern("failed to process NameNode ${this}") { this }
    }
}

val NameNode.size: Int
    get() = countDepth(0)


fun String.toNameNode(): NameNode {
    return split(".").map { IdentifierNode(it) }.reduce<NameNode, IdentifierNode> { acc, identifier -> identifier.appendRight(acc) }
}


