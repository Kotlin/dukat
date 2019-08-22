package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

data class ModuleDeclaration(
        val packageName: NameEntity,
        val declarations: List<TopLevelEntity> = emptyList(),

        val modifiers: List<ModifierDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        val uid: String,
        val resourceName: String,
        val root: Boolean
) : TopLevelDeclaration