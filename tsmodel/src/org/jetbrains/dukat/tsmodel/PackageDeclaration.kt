package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class PackageDeclaration(
        val packageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList(),

        val modifiers: List<ModifierDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        val uid: String,
        val resourceName: String,
        val root: Boolean
) : TopLevelDeclaration