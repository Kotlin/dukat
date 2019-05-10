package org.jetbrains.dukat.ast.model.nodes

interface NameNode : HeritageSymbolNode

fun  NameNode.translate(): String = when (this) {
    is IdentifierNode -> value
    is QualifiedNode -> {
        "${left.translate()}.${right.translate()}" + (if (nullable) "?" else "")
    }
    is GenericIdentifierNode -> value + "<${typeParameters.joinToString(", ") { typeParameter -> typeParameter.value.translate() }}>"
    else -> throw Exception("unknown NameNode ${this}")
}


private fun NameNode.countDepth(current: Int): Int {
    return when(this) {
        is IdentifierNode -> current + 1
        is QualifiedNode -> left.countDepth(current) + right.countDepth(current)
        else -> throw Exception("unknown NameNode ${this}")
    }
}

fun NameNode.process(handler: (String) -> String): NameNode {
    return when(this) {
        is IdentifierNode -> IdentifierNode(handler(value))
        is QualifiedNode -> copy(left = left.process(handler), right = right.process(handler) as IdentifierNode)
        else -> throw Exception("failed to process NameNode ${this}")
    }
}

val NameNode.size: Int
    get() = countDepth(0)


fun String.toNameNode(): NameNode {
    return split(".").map{IdentifierNode(it)}.reduce<NameNode, IdentifierNode> { acc, identifier -> identifier.appendRight(acc)}
}


