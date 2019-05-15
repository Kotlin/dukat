package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity

data class ImportNode(
    val referenceName: NameNode,
    val uid: String
) : Entity