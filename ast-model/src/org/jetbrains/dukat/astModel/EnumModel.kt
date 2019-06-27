package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity

data class EnumModel(
        override val name: NameEntity,
        val values: List<EnumTokenModel>
) : TopLevelNode