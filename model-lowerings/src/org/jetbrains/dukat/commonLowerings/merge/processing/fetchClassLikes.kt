package org.jetbrains.dukat.commonLowerings.merge.processing

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ModuleModel

fun ModuleModel.fetchClassLikes(): List<ModelWithOwnerName<ClassLikeModel>> {
    return declarations.filterIsInstance(ClassLikeModel::class.java).map { ModelWithOwnerName(it, name.appendLeft(it.name)) } +
            submodules.flatMap { submodule -> submodule.fetchClassLikes() }
}

private fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> value
    is QualifierEntity -> {
        "${left.translate()}.${right.translate()}"
    }
}

fun ModuleModel.fetchModules(): List<ModelWithOwnerName<ModuleModel>> {
    return listOf(ModelWithOwnerName(this, name)) + submodules.flatMap { it.fetchModules() }
}