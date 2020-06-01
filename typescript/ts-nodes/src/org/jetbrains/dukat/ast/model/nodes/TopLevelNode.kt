package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelEntity

interface TopLevelNode : TopLevelEntity, UniqueNode {
    val external: Boolean
}