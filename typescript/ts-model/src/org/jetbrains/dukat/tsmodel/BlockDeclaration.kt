package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelEntity

data class BlockDeclaration(
        val statements: List<TopLevelEntity>
) : TopLevelEntity