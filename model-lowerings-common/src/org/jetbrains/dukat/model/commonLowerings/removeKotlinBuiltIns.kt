package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.CanBeExtensionModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.stdlib.KotlinBuiltInEntities
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities

class RemoveKotlinBuiltIns() : StdlibModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return module.copy(declarations = module.declarations.filter {
            if (it is ClassLikeModel) {
                !KotlinBuiltInEntities.contains(it.name) && !KotlinStdlibEntities.contains(it.name)
            } else if (it is CanBeExtensionModel) {
                it.extend?.name?.let { name ->
                    !KotlinStdlibEntities.contains(name)
                } ?: true
            } else {
                true
            }
        })
    }
}