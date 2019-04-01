package org.jetbrains.dukat.ast.model.nodes

interface NameNode : HeritageSymbolNode

fun  NameNode.translate(): String = when (this) {
    is IdentifierNode -> value
    is QualifiedNode -> "${left.translate()}.${right.translate()}"
    is GenericIdentifierNode -> value + "<${typeParameters.joinToString(", ") { typeParameter -> typeParameter.name }}>"
    else -> throw Exception("unknown NameNode ${this}")
}


private fun NameNode.countDepth(current: Int): Int {
    return when(this) {
        is IdentifierNode -> current + 1
        is QualifiedNode -> left.countDepth(current) + right.countDepth(current)
        else -> throw Exception("unknown NameNode ${this}")
    }
}

val NameNode.size: Int
    get() = countDepth(0)



