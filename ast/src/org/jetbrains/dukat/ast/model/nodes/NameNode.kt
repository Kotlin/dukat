package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Declaration

interface NameNode : Declaration

fun  NameNode.translate(): String = when (this) {
    is IdentifierNode -> value
    is QualifiedNode -> "${left.debugTranslate()}.${right.translate()}"
    else -> throw Exception("unknown FunctionNodeName ${this}")
}

