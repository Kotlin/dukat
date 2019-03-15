package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration

data class DocumentRootNode(
        val fileName: String,
        val resourceName: String,

        val packageName: String,
        var fullPackageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList(),
        val imports: Map<String, ModuleReferenceDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,

        var owner: DocumentRootNode?,
        var uid: String,

        var qualifierName: String = "",
        var isQualifier: Boolean = false,
        var showQualifierAnnotation: Boolean = false
) : TopLevelDeclaration
