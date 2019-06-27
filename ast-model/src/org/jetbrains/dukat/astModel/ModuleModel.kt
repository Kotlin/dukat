package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

data class ModuleModel(
        override val name: NameEntity,
        val shortName: NameEntity,
        val declarations: List<TopLevelModel> = emptyList(),
        val annotations: MutableList<AnnotationModel>,
        val sumbodules: List<ModuleModel>,
        val imports: MutableList<NameEntity>
) : TopLevelModel

fun ModuleModel.flattenDeclarations(): List<ModuleModel> {
    return (listOf(this.copy(sumbodules = emptyList())) + sumbodules.flatMap { submodule -> submodule.flattenDeclarations() })
            .filter { module -> module.declarations.isNotEmpty() }
}