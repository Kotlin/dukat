package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.transform


fun ModuleModel.mergeVarsAndInterfaces(): ModuleModel {

    // TODO: investigate where we ever will see multiple variables with same name
    val mergeMap = mutableMapOf<NameEntity, TopLevelModel?>()
    declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceModel -> mergeMap[declaration.name] = null
        }
    }


    declarations.forEach { declaration ->
        if (declaration is VariableModel) {
            if (mergeMap.containsKey(declaration.name)) {
                mergeMap[declaration.name] = declaration
            }
        } else if (declaration is ObjectModel) {
            if (mergeMap.containsKey(declaration.name)) {
                mergeMap[declaration.name] = declaration
            }
        }
    }

    val declarationsMerged = declarations.flatMap { declaration ->
        when (declaration) {
            is VariableModel -> {
                val correspondingInterface = mergeMap[declaration.name]
                if (correspondingInterface == null) {
                    listOf(declaration)
                } else {
                    emptyList()
                }
            }
            is ObjectModel -> {
                val correspondingInterface = mergeMap[declaration.name]
                if (correspondingInterface == null) {
                    listOf(declaration)
                } else {
                    emptyList()
                }
            }
            is InterfaceModel -> {
                val correspondingEntity = mergeMap[declaration.name]
                if (correspondingEntity == null) {
                    listOf(declaration)
                } else {
                    if ((correspondingEntity is VariableModel) && (correspondingEntity.type is TypeValueModel)) {
                        listOf(declaration.copy(
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
                        ))
                    } else if (correspondingEntity is ObjectModel) {
                        listOf(declaration.copy(
                                companionObject = correspondingEntity
                        ))
                    } else listOf(declaration)
                }
            }
            else -> listOf(declaration)
        }
    }

    return copy(declarations = declarationsMerged, submodules = submodules.map(ModuleModel::mergeVarsAndInterfaces))
}

fun SourceSetModel.mergeVarsAndInterfaces() = transform { it.mergeVarsAndInterfaces() }