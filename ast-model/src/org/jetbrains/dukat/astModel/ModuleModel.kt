package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

data class ModuleModel(
        override val name: NameEntity,
        val shortName: NameEntity,
        val declarations: List<TopLevelModel> = emptyList(),
        val annotations: MutableList<AnnotationModel>,
        val submodules: List<ModuleModel>,
        val imports: MutableList<NameEntity>
) : TopLevelModel

fun ModuleModel.flattenDeclarations(): List<ModuleModel> {
    return (listOf(this.copy(submodules = emptyList())) + submodules.flatMap { submodule -> submodule.flattenDeclarations() })
            .filter { module -> module.declarations.isNotEmpty() && !module.declarations.all { it is InterfaceModel && it.fromStdlib }}
}