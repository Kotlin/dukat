package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Declaration

data class ImportNode(
    val referenceName: NameNode,
    val uid: String
) : Declaration