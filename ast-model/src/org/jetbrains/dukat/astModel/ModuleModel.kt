package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class ModuleModel(
        val qualifiedName: NameNode?,
        val packageName: NameNode,
        val shortName: String,
        val declarations: List<TopLevelNode> = emptyList(),
        val annotations: MutableList<AnnotationNode>,
        val sumbodules: List<ModuleModel>,
        val imports: MutableList<NameNode>
) : TopLevelDeclaration, TopLevelNode

fun ModuleModel.flattenDeclarations(): List<ModuleModel> {
    return (listOf(this) + sumbodules.flatMap { submodule -> submodule.flattenDeclarations() })
            .filter { module -> module.declarations.isNotEmpty() }
}