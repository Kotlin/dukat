package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class ModuleNode(
        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelNode> = emptyList(),
        val imports: Map<String, ImportNode>,

        override var uid: String,
        override val external: Boolean
) : TopLevelNode
