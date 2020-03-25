package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity

data class ModuleNode(
        val moduleName: NameEntity?,

        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelNode> = emptyList(),
        val imports: Map<String, ImportNode>,

        val moduleNameIsStringLiteral: Boolean,

        var jsModule: NameEntity?,
        var jsQualifier: NameEntity?,

        var uid: String,
        val root: Boolean,
        override val external: Boolean

) : TopLevelNode
