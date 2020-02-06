package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.importClause.ImportDeclaration
import org.jetbrains.dukat.tsmodel.importClause.ReferenceClauseDeclaration

data class ModuleDeclaration(
        val packageName: NameEntity,
        val imports: List<ImportDeclaration> = emptyList(),
        val references: List<ReferenceClauseDeclaration>,
        val declarations: List<TopLevelDeclaration> = emptyList(),

        val modifiers: List<ModifierDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String,
        val resourceName: String,
        val root: Boolean
) : TopLevelDeclaration, FunctionOwnerDeclaration, WithUidDeclaration