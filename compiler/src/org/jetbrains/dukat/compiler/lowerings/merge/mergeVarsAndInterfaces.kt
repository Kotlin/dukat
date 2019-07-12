package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.*


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
                            companionObject = CompanionObjectModel(
                                    "__",
                                    emptyList(),
                                    listOf(
                                            HeritageModel(
                                                    correspondingVariable.type as TypeValueModel,
                                                    emptyList(),
                                                    ExternalDelegationModel()
                                            )
                                    )
                            )))
                }
            }
            else -> listOf(declaration)
        }
    }

    return copy(declarations = declarationsMerged, sumbodules = sumbodules.map(ModuleModel::mergeVarsAndInterfaces))
}

fun SourceSetModel.mergeVarsAndInterfaces() = transform { it.mergeVarsAndInterfaces() }