package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity

data class DocumentRootNode(
        val moduleName: NameEntity?,

        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelNode> = emptyList(),
        val imports: Map<String, ImportNode>,

        val external: Boolean,

        var jsModule: NameEntity?,
        var jsQualifier: NameEntity?,

        var uid: String,
        val root: Boolean
) : TopLevelNode
