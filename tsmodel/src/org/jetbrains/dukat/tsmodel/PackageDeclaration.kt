package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelEntity

data class PackageDeclaration(
        val packageName: String,
        val declarations: List<TopLevelEntity> = emptyList(),

        val modifiers: List<ModifierDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        val uid: String,
        val resourceName: String,
        val root: Boolean
) : TopLevelEntity