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
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelLowering

private fun getKey(onwnerName: NameEntity, name: NameEntity): Pair<NameEntity, NameEntity> {
    val owner = when (onwnerName) {
        IdentifierEntity("<ROOT>") -> IdentifierEntity("<DEFAULT>")
        IdentifierEntity("<LIBROOT>") -> IdentifierEntity("<DEFAULT>")
        else -> onwnerName
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

fun ModuleModel.mergeVarsAndInterfaces(mergeMap: Map<Pair<NameEntity, NameEntity>, TopLevelModel?>): ModuleModel {

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
                                                null
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

private fun SourceSetModel.mergeVarsAndInterfaces(): SourceSetModel {
    val mergeMap = mutableMapOf<Pair<NameEntity, NameEntity>, TopLevelModel?>()
    sources.forEach { it.root.scanInterfaces(mergeMap) }
    sources.forEach { it.root.scanVariablesAndObjects(mergeMap) }

    return transform {
        // TODO: investigate where we ever will see multiple variables with same name
        it.mergeVarsAndInterfaces(mergeMap)
    }
}

class MergeVarsAndInterfaces() : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.mergeVarsAndInterfaces()
    }
}