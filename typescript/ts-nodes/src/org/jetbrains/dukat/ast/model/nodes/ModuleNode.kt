package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

data class ModuleNode(
        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelDeclaration> = emptyList(),
        val imports: Map<String, ImportNode>,

        var uid: String,
        val external: Boolean
) : TopLevelDeclaration
