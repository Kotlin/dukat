package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.MergableNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.compiler.model.ROOT_CLASS_DECLARATION


private fun ModuleModel.canBeMerged(): Boolean {
    return declarations.any { declaration -> declaration is MemberDeclaration}
}

private fun VariableNode.convert() : MemberDeclaration {
    return PropertyNode(
        name = name,
        type = type,
        typeParameters = emptyList(),
        owner = ROOT_CLASS_DECLARATION,
        static = false,
        override = false,
        getter = false,
        setter = false,
        open = false
    )
}

private fun FunctionNode.convert() : MemberDeclaration {
    return MethodNode(
        name = name,
        parameters = parameters,
        type = type,
        typeParameters = typeParameters,
        owner = ROOT_CLASS_DECLARATION,
        static = false,
        override = false,
        operator = false,
        annotations = annotations,
        open = false
    )
}

private fun MergableNode.convert(): MemberDeclaration {
    return when(this) {
        is FunctionNode -> convert()
        is VariableNode -> convert()
        else -> throw Error("can not convert unknown MergableNode ${this}")
    }
}

fun ModuleModel.mergeDeclarations(): ModuleModel {

    val interfaces = mutableMapOf<String, InterfaceModel>()

    declarations.forEach { declaration ->
        if (declaration is InterfaceModel) {
            interfaces.put(declaration.name, declaration)
        }
    }

    val modulesToBeMerged = mutableMapOf<String, MutableList<ModuleModel>>()

    val resolvedSubmodules = sumbodules.map { subModule ->
        when (subModule) {
            is ModuleModel -> {
                if ((interfaces.containsKey(subModule.shortName)) && (subModule.canBeMerged())) {
                    val bucket = modulesToBeMerged.getOrPut(subModule.shortName) { mutableListOf() }
                    bucket.add(subModule)
                    emptyList()
                } else listOf(subModule)
            }
            else -> listOf(subModule)
        }
    }.flatten()


    val mergedDeclarations = declarations.map { declaration ->
        when (declaration) {
            is InterfaceModel -> {
                val members = declaration.companionObject.members.toMutableList()
                modulesToBeMerged.getOrDefault(declaration.name, mutableListOf()).forEach { module ->
                    val submoduleDecls = module.declarations
                            .filterIsInstance(MergableNode::class.java)
                            .map { it.convert() }
                    members.addAll(submoduleDecls)
                }

                declaration.copy(companionObject = declaration.companionObject.copy(members = members))
            }
            else -> declaration
        }
    }


    return copy(declarations = mergedDeclarations, sumbodules = resolvedSubmodules)
}