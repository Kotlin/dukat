package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.importClause.ImportDeclaration
import org.jetbrains.dukat.tsmodel.importClause.ReferenceClauseDeclaration

data class ModuleDeclaration(
        val packageName: NameEntity,
        val imports: List<ImportDeclaration>,
        val references: List<ReferenceClauseDeclaration>,
        val declarations: List<TopLevelDeclaration>,

        override val modifiers: List<ModifierDeclaration>,

        override val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String,
        val resourceName: String,
        val kind: ModuleDeclarationKind
) : TopLevelDeclaration, FunctionOwnerDeclaration, WithModifiersDeclaration, MergeableDeclaration