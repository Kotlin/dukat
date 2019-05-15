package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.transform


fun ModuleModel.mergeVarsAndInterfaces(): ModuleModel {

    // TODO: investigate where we ever will see multiple variables with same name
    val mergeMap = mutableMapOf<String, VariableModel?>()
    declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceModel -> mergeMap[declaration.name] = null
        }
    }


    declarations.forEach { declaration ->
        if (declaration is VariableModel) {
            if (mergeMap.containsKey(declaration.name.translate())) {
                mergeMap[declaration.name.translate()] = declaration
            }
        }
    }


    val declarationsMerged = declarations.flatMap { declaration ->
        when (declaration) {
            is VariableModel -> {
                val correspondingInterface = mergeMap.get(declaration.name.translate())
                if (correspondingInterface == null) {
                    listOf(declaration)
                } else {
                    emptyList()
                }
            }
            is InterfaceModel -> {
                val correspondingVariable = mergeMap.get(declaration.name)
                if (correspondingVariable == null) {
                    listOf(declaration)
                } else {
                    listOf(declaration.copy(
                            companionObject = CompanionObjectModel(
                                    "__",
                                    emptyList(),
                                    listOf(
                                            HeritageModel(correspondingVariable.type, emptyList(), ExternalDelegationModel())
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