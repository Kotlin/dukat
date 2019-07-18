package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class ImportNode(
    val referenceName: NameEntity,
    val uid: String
) : Entity