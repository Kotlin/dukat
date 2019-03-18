package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration

data class DocumentRootNode(
        val fileName: String,
        val resourceName: String,

        val packageName: String,
        var fullPackageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList(),
        val imports: Map<String, ImportNode>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,

        var owner: DocumentRootNode?,
        var uid: String,

        var qualifiedNode: NameNode?,
        var isQualifier: Boolean,
        var showQualifierAnnotation: Boolean
) : TopLevelDeclaration
