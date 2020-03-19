package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.astCommon.TopLevelEntity

interface TopLevelNode : TopLevelEntity {
    val external: Boolean
}