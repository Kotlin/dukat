package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.transform


fun ModuleModel.mergeVarsAndInterfaces(): ModuleModel {

    // TODO: investigate where we ever will see multiple variables with same name
    val mergeMap = mutableMapOf<NameEntity, VariableModel?>()
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
        }
    }

    val declarationsMerged = declarations.flatMap { declaration ->
        when (declaration) {
            is VariableModel -> {
                val correspondingInterface = mergeMap.get(declaration.name)
                if (correspondingInterface == null) {
                    listOf(declaration)
                } else {
                    emptyList()
                }
            }
            is InterfaceModel -> {
                val correspondingVariable = mergeMap.get(declaration.name)
                if (correspondingVariable == null || correspondingVariable.type !is TypeValueModel) {
                    listOf(declaration)
                } else {
                    listOf(declaration.copy(
                            companionObject = ObjectModel(
                                IdentifierEntity("__"),
                                    emptyList(),
                                    listOf(
                                            HeritageModel(
                                                    correspondingVariable.type as TypeValueModel,
                                                    emptyList(),
                                                    ExternalDelegationModel()
                                            )
                                    ),
                                    VisibilityModifierModel.DEFAULT
                            )
                            ))
                }
            }
            else -> listOf(declaration)
        }
    }

    return copy(declarations = declarationsMerged, submodules = submodules.map(ModuleModel::mergeVarsAndInterfaces))
}

fun SourceSetModel.mergeVarsAndInterfaces() = transform { it.mergeVarsAndInterfaces() }