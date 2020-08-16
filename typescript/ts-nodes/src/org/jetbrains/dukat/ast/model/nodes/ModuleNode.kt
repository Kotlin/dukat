package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

data class ModuleNode(
        val packageName: NameEntity,
        var qualifiedPackageName: NameEntity,
        val declarations: List<TopLevelDeclaration> = emptyList(),

        var uid: String,
        val kind: ModuleDeclarationKind
) : TopLevelDeclaration
