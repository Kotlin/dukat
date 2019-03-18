package org.jetbrains.dukat.ast.model.nodes

interface NameNode : HeritageSymbolNode

fun  NameNode.translate(): String = when (this) {
    is IdentifierNode -> value
    is QualifiedNode -> "${left.debugTranslate()}.${right.translate()}"
    else -> throw Exception("unknown FunctionNodeName ${this}")
}

