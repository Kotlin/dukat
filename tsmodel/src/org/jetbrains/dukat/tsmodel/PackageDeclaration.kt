package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstTopLevelEntity

data class PackageDeclaration(
        val packageName: String,
        val declarations: List<AstTopLevelEntity> = emptyList(),

        val modifiers: List<ModifierDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        val uid: String,
        val resourceName: String,
        val root: Boolean
) : AstTopLevelEntity