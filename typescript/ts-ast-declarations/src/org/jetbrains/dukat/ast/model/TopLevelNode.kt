package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.ast.model.nodes.UniqueNode
import org.jetbrains.dukat.astCommon.TopLevelEntity

interface TopLevelNode : TopLevelEntity, UniqueNode {
    val external: Boolean
}