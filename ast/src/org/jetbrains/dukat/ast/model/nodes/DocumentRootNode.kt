package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class DocumentRootNode(
        val packageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList(),

        var owner: DocumentRootNode?
) : TopLevelDeclaration