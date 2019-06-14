package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration

data class DocumentRootNode(
        val fileName: String,

        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelEntity> = emptyList(),
        val imports: Map<String, ImportNode>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,

        val external: Boolean,

        var jsModule: NameEntity?,
        var jsQualifier: NameEntity?,

        var owner: DocumentRootNode?,
        var uid: String
) : TopLevelEntity
