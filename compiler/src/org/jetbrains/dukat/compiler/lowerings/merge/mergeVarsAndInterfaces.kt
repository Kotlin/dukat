package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.CompanionObjectModel
import org.jetbrains.dukat.ast.model.model.ExternalDelegationModel
import org.jetbrains.dukat.ast.model.model.HeritageModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.VariableNode


fun ModuleModel.mergeVarsAndInterfaces(): ModuleModel {

    // TODO: investigate where we ever will see multiple variables with same name
    val mergeMap = mutableMapOf<String, VariableNode?>()
    declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceModel -> mergeMap.put(declaration.name, null)
        }
    }


    declarations.forEach { declaration ->
        if (declaration is VariableNode) {
            if (mergeMap.containsKey(declaration.name)) {
                mergeMap[declaration.name] = declaration
            }
        }
    }


    val declarationsMerged = declarations.flatMap { declaration ->
        when (declaration) {
            is VariableNode -> {
                val correspondingInterface = mergeMap.get(declaration.name)
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
                                        HeritageModel(correspondingVariable.type, ExternalDelegationModel())
                                    )
                    )))
                }
            }
            else -> listOf(declaration)
        }
    }

    return copy(declarations = declarationsMerged, sumbodules = sumbodules.map(ModuleModel::mergeVarsAndInterfaces))
}