package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.transform


private fun ModuleModel.merge(module: ModuleModel): ModuleModel {
    return copy(
            declarations = (declarations + module.declarations),
            submodules = (submodules + module.submodules)
    )
}


fun ModuleModel.mergeModules(): ModuleModel {

    val modulesInBuckets = mutableMapOf<NameEntity, MutableList<ModuleModel>>()

    submodules.forEach { submodule ->
        modulesInBuckets.getOrPut(submodule.shortName) { mutableListOf() }.add(submodule)
    }

    val modulesInBucketsMerged = modulesInBuckets
            .mapValues { entry -> entry.value.reduceRight { module, acc -> module.merge(acc) } }
            .toMutableMap()

    val submodulesResolved = mutableListOf<ModuleModel>()
    submodules.forEach { submodule ->
        val submodule = modulesInBucketsMerged.remove(submodule.shortName)
        if (submodule != null) {
            submodulesResolved.add(submodule.mergeModules())
        }
    }

    return copy(submodules = submodulesResolved)
}

fun SourceSetModel.mergeModules() = transform { it.mergeModules() }