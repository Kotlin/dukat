package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class ModuleModel(
        override val name: NameEntity,
        val shortName: NameEntity,
        val declarations: List<TopLevelModel> = emptyList(),
        val annotations: MutableList<AnnotationModel>,
        val submodules: List<ModuleModel>,
        val imports: MutableList<ImportModel>,
        override val comment: CommentEntity?
) : TopLevelModel {
    override val visibilityModifier: VisibilityModifierModel
        get() = VisibilityModifierModel.DEFAULT
}

fun ModuleModel.flattenDeclarations(): List<ModuleModel> {
    return (listOf(this.copy(submodules = emptyList())) + submodules.flatMap { submodule -> submodule.flattenDeclarations() })
            .filter { module -> module.declarations.isNotEmpty() }
}