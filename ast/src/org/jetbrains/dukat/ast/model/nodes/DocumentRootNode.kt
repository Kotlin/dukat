package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class DocumentRootNode(
        val packageName: String,
        var fullPackageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList(),

        var owner: DocumentRootNode?,

        var qualifierName: String = "",
        var isQualifier: Boolean = false,
        var showQualifierAnnotation: Boolean = false
) : TopLevelDeclaration