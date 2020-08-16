package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.ModuleDeclaration

data class SourceFileNode(
        val fileName: String,
        val root: ModuleDeclaration,
        val referencedFiles: List<String>,
        val name: NameEntity?
) : Entity