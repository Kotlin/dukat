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

private fun ModuleModel.scan(mergeMap: MutableMap<Pair<NameEntity, NameEntity>, TopLevelModel?>) {
    declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceModel -> mergeMap[Pair(name, declaration.name)] = null
        }
    }

    declarations.forEach { declaration ->
        if ((declaration is VariableModel) || (declaration is ObjectModel)) {
            if (mergeMap.containsKey(Pair(name, declaration.name))) {
                mergeMap[Pair(name, declaration.name)] = declaration
            }
        }
    }

    submodules.forEach { it.scan(mergeMap) }
}

fun ModuleModel.mergeVarsAndInterfaces(mergeMap: Map<Pair<NameEntity, NameEntity>, TopLevelModel?>): ModuleModel {
    val declarationsMerged = declarations.mapNotNull { declaration ->
        when (declaration) {
            is VariableModel -> {
                if (!mergeMap.containsKey(Pair(name, declaration.name))) {
                    declaration
                } else {
                    null
                }
            }
            is ObjectModel -> {
                if (!mergeMap.containsKey(Pair(name, declaration.name))) {
                    declaration
                } else {
                    null
                }
            }
            is InterfaceModel -> {
                mergeMap[Pair(name, declaration.name)]?.let { correspondingEntity ->
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

    return copy(declarations = declarationsMerged, submodules = submodules.map { it.mergeVarsAndInterfaces(mergeMap) } )
}

fun SourceSetModel.mergeVarsAndInterfaces(): SourceSetModel {
    return transform {
        // TODO: investigate where we ever will see multiple variables with same name
        val mergeMap = mutableMapOf<Pair<NameEntity, NameEntity>, TopLevelModel?>()
        it.scan(mergeMap)
        it.mergeVarsAndInterfaces(mergeMap)
    }
}