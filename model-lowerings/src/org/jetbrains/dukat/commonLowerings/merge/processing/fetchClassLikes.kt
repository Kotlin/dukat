package org.jetbrains.dukat.commonLowerings.merge.processing

import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ModuleModel

fun ModuleModel.fetchClassLikes(): List<ModelWithOwnerName<ClassLikeModel>> {
    return declarations.filterIsInstance(ClassLikeModel::class.java).map { ModelWithOwnerName(it, name.appendLeft(it.name)) } +
            submodules.flatMap { submodule -> submodule.fetchClassLikes() }
}

fun ModuleModel.fetchModules(): List<ModelWithOwnerName<ModuleModel>> {
    return listOf(ModelWithOwnerName(this, name)) + submodules.flatMap { it.fetchModules() }
}