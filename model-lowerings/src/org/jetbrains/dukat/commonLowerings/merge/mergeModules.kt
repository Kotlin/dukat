package org.jetbrains.dukat.commonLowerings.merge

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
    val submodulesMerged = submodules
            .groupBy { it.shortName }
            .values.map { value -> value.reduceRight { module, acc -> module.merge(acc) }.mergeModules() }

    return copy(submodules = submodulesMerged)
}

fun SourceSetModel.mergeModules() = transform { it.mergeModules() }