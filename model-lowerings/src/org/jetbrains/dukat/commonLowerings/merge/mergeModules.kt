package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering


private fun ModuleModel.merge(module: ModuleModel): ModuleModel {
    return copy(
            declarations = (declarations + module.declarations),
            submodules = (submodules + module.submodules)
    )
}

private fun ModuleModel.mergeModules(): ModuleModel {
    val submodulesMerged = submodules
            .groupBy { it.shortName }
            .values.map { value -> value.reduceRight { module, acc -> module.merge(acc) }.mergeModules() }

    return copy(submodules = submodulesMerged)
}

class MergeModules : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return module.mergeModules()
    }
}