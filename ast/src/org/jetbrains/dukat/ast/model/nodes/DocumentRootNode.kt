package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration

data class DocumentRootNode(
        val packageName: String,
        var fullPackageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList(),
        val imports: List<ImportEqualsDeclaration>,

        var owner: DocumentRootNode?,
        var uid: String,

        var qualifierName: String = "",
        var isQualifier: Boolean = false,
        var showQualifierAnnotation: Boolean = false
) : TopLevelDeclaration