package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.stdlib.TSLIBROOT

private fun getKey(ownerName: NameEntity, name: NameEntity): Pair<NameEntity, NameEntity> {
    val owner = when (ownerName) {
        IdentifierEntity("<ROOT>") -> IdentifierEntity("<DEFAULT>")
        TSLIBROOT -> IdentifierEntity("<DEFAULT>")
        else -> ownerName
    }
    return Pair(owner, name)
}

private fun ModuleModel.scanInterfaces(mergeMap: MutableMap<Pair<NameEntity, NameEntity>, TopLevelModel?>) {
    declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceModel -> mergeMap[getKey(name, declaration.name)] = null
        }
    }

    submodules.forEach { it.scanInterfaces(mergeMap) }
}

private fun ModuleModel.scanVariablesAndObjects(mergeMap: MutableMap<Pair<NameEntity, NameEntity>, TopLevelModel?>) {

    declarations.forEach { declaration ->
        if ((declaration is VariableModel) || (declaration is ObjectModel)) {
            val key = getKey(name, declaration.name)
            if (mergeMap.containsKey(key)) {
                mergeMap[key] = declaration
            }
        }
    }

    submodules.forEach { it.scanVariablesAndObjects(mergeMap) }
}

private fun ModuleModel.mergeVarsAndInterfaces(mergeMap: Map<Pair<NameEntity, NameEntity>, TopLevelModel?>): ModuleModel {

    val declarationsMerged = declarations.mapNotNull { declaration ->
        when (declaration) {
            is VariableModel -> {
                if (!mergeMap.containsKey(getKey(name, declaration.name))) {
                    declaration
                } else {
                    null
                }
            }
            is ObjectModel -> {
                if (!mergeMap.containsKey(getKey(name, declaration.name))) {
                    declaration
                } else {
                    null
                }
            }
            is InterfaceModel -> {
                mergeMap[getKey(name, declaration.name)]?.let { correspondingEntity ->
                    when (correspondingEntity) {
                        is VariableModel -> {
                            if (correspondingEntity.type is TypeValueModel) {
                                declaration.copy(
                                        companionObject = ObjectModel(
                                                IdentifierEntity("__"),
                                                emptyList(),
                                                listOf(
                                                        HeritageModel(
                                                                correspondingEntity.type as TypeValueModel,
                                                                emptyList(),
                                                                ExternalDelegationModel()
                                                        )
                                                ),
                                                VisibilityModifierModel.DEFAULT,
                                                null,
                                                external = false
                                        )
                                )
                            } else null
                        }
                        is ObjectModel -> declaration.copy(
                                companionObject = correspondingEntity
                        )
                        else -> null
                    }
                } ?: declaration
            }
            else -> declaration
        }
    }

    return copy(declarations = declarationsMerged, submodules = submodules.map { it.mergeVarsAndInterfaces(mergeMap) })
}

class MergeVarsAndInterfaces : ModelLowering {
    private val mergeMap = mutableMapOf<Pair<NameEntity, NameEntity>, TopLevelModel?>()

    override fun lower(module: ModuleModel): ModuleModel {
        return module.mergeVarsAndInterfaces(mergeMap)
    }

    override fun lower(source: SourceSetModel): SourceSetModel {
        source.sources.forEach { it.root.scanInterfaces(mergeMap) }
        source.sources.forEach { it.root.scanVariablesAndObjects(mergeMap) }
        return super.lower(source)
    }
}