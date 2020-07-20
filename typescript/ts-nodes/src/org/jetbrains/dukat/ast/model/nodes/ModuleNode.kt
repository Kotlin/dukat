package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity

data class ModuleNode(
        val moduleName: NameEntity?,
        val export: ExportAssignmentNode?,

        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelNode> = emptyList(),
        val imports: Map<String, ImportNode>,

        val moduleNameIsStringLiteral: Boolean,

        val jsModule: NameEntity?,
        val jsQualifier: NameEntity?,

        override var uid: String,
        override val external: Boolean

) : TopLevelNode
