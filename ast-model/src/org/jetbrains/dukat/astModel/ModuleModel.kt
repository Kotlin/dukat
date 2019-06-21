package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

data class ModuleModel(
        override val name: NameEntity,
        val shortName: NameEntity,
        val declarations: List<TopLevelNode> = emptyList(),
        val annotations: MutableList<AnnotationNode>,
        val sumbodules: List<ModuleModel>,
        val imports: MutableList<NameEntity>
) : TopLevelEntity, TopLevelNode

fun ModuleModel.flattenDeclarations(): List<ModuleModel> {
    return (listOf(this.copy(sumbodules = emptyList())) + sumbodules.flatMap { submodule -> submodule.flattenDeclarations() })
            .filter { module -> module.declarations.isNotEmpty() }
}