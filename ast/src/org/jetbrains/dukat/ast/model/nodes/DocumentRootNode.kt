package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstTopLevelEntity
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration

data class DocumentRootNode(
        val fileName: String,
        val resourceName: String,

        val packageName: String,
        var fullPackageName: NameNode,
        val declarations: List<AstTopLevelEntity> = emptyList(),
        val imports: Map<String, ImportNode>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,

        var owner: DocumentRootNode?,
        var uid: String,

        var qualifiedNode: NameNode?,
        var isQualifier: Boolean,
        var showQualifierAnnotation: Boolean
) : AstTopLevelEntity
