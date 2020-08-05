package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class ModuleNode(
        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelNode> = emptyList(),
        val imports: Map<String, ImportNode>,

        val jsModule: NameEntity?,
        val jsQualifier: NameEntity?,

        override var uid: String,
        override val external: Boolean
) : TopLevelNode
