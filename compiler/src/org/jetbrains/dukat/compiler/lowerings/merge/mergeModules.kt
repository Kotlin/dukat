package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.transform


private fun ModuleModel.merge(module: ModuleModel): ModuleModel {
    return copy(
            declarations = (declarations + module.declarations),
            sumbodules = (sumbodules + module.sumbodules)
    )
}


fun ModuleModel.mergeModules(): ModuleModel {

    val modulesInBuckets = mutableMapOf<NameEntity, MutableList<ModuleModel>>()

    sumbodules.forEach { submodule ->
        modulesInBuckets.getOrPut(submodule.shortName) { mutableListOf() }.add(submodule)
    }

    val modulesInBucketsMerged = modulesInBuckets
            .mapValues { entry -> entry.value.reduceRight { module, acc -> module.merge(acc) } }
            .toMutableMap()

    val submodulesResolved = mutableListOf<ModuleModel>()
    sumbodules.forEach { submodule ->
        val submodule = modulesInBucketsMerged.remove(submodule.shortName)
        if (submodule != null) {
            submodulesResolved.add(submodule.mergeModules())
        }
    }

    return copy(sumbodules = submodulesResolved)
}

fun SourceSetModel.mergeModules() = transform { it.mergeModules() }