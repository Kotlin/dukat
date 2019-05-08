package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstEntity

data class ImportNode(
    val referenceName: NameNode,
    val uid: String
) : AstEntity