package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class SourceFileNode(
        val fileName: String,
        val root: ModuleNode,
        val referencedFiles: List<String>,
        val name: NameEntity?
) : Entity


fun SourceFileNode.transform(rootHandler: (ModuleNode) -> ModuleNode)
    = copy(root = rootHandler(root))
