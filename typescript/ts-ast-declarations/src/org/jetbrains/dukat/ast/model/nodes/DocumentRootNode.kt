package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

data class DocumentRootNode(
        val moduleName: NameEntity?,

        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelEntity> = emptyList(),
        val imports: Map<String, ImportNode>,

        val external: Boolean,

        var jsModule: NameEntity?,
        var jsQualifier: NameEntity?,

        var uid: String,
        val root: Boolean
) : TopLevelEntity
