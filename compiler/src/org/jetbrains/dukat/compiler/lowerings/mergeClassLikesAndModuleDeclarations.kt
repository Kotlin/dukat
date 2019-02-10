package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.MergableNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.compiler.model.ROOT_CLASS_DECLARATION


private fun ModuleModel.canBeMerged(): Boolean {
    return declarations.any { declaration -> declaration is MemberDeclaration }
}

private fun VariableNode.convert(): MemberDeclaration {
    return PropertyNode(
            name = name,
            type = type,
            typeParameters = emptyList(),
            owner = ROOT_CLASS_DECLARATION,
            static = false,
            override = false,
            getter = false,
            setter = false,
            open = false,
            definedExternally = true
    )
}

private fun FunctionNode.convert(): MemberDeclaration {
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
            open = false,
            definedExternally = true
    )
}

private fun MergableNode.convert(): MemberDeclaration {
    return when (this) {
        is FunctionNode -> convert()
        is VariableNode -> convert()
        else -> throw Error("can not convert unknown MergableNode ${this}")
    }
}

private fun ObjectNode.merge(ownerName: String, modulesToBeMerged: Map<String, MutableList<ModuleModel>>): ObjectNode {
    val members = members.toMutableList()
    modulesToBeMerged.getOrDefault(ownerName, mutableListOf()).forEach { module ->
        val submoduleDecls = module.declarations
                .filterIsInstance(MergableNode::class.java)
                .map { it.convert() }
        members.addAll(submoduleDecls)
    }

    return copy(members = members)
}

private fun collectModelsToBeMerged(submodules: List<ModuleModel>, context: Map<String, ClassLikeNode>, modulesToBeMerged: MutableMap<String, MutableList<ModuleModel>>): List<ModuleModel> {

    return submodules.map { subModule ->
        if ((context.containsKey(subModule.shortName)) && (subModule.canBeMerged())) {
            val bucket = modulesToBeMerged.getOrPut(subModule.shortName) { mutableListOf() }
            bucket.add(subModule)
            emptyList()
        } else listOf(subModule)
    }.flatten()

}

fun InterfaceModel.merge(interfaceModel: InterfaceModel): InterfaceModel {
    return copy(
            members = members + interfaceModel.members,
            companionObject = companionObject.copy(members = companionObject.members + interfaceModel.companionObject.members)
    )
}


fun ModuleModel.mergeClassLikesAndModuleDeclarations(): ModuleModel {
    val interfacesInBucket = mutableMapOf<String, MutableList<InterfaceModel>>()

    val classes = mutableMapOf<String, ClassModel>()

    declarations.forEach { declaration ->
        if (declaration is InterfaceModel) {
            interfacesInBucket.getOrPut(declaration.name) { mutableListOf() }.add(declaration)
        } else if (declaration is ClassModel) {
            classes.put(declaration.name, declaration)
        }
    }


    val interfaces = interfacesInBucket.mapValues { entry -> entry.value.reduceRight { interfaceModel, acc -> interfaceModel.merge(acc) } }.toMutableMap()

    val modulesToBeMergedWithInterfaces = mutableMapOf<String, MutableList<ModuleModel>>()
    val modulesToBeMergedWithClasses = mutableMapOf<String, MutableList<ModuleModel>>()

    var resolvedSubmodules = collectModelsToBeMerged(sumbodules, interfaces, modulesToBeMergedWithInterfaces)
    resolvedSubmodules = collectModelsToBeMerged(resolvedSubmodules, classes, modulesToBeMergedWithClasses)
            .map { moduleModel -> moduleModel.mergeClassLikesAndModuleDeclarations() }

    val mergedDeclarations = declarations
            .map { declaration ->
                when (declaration) {
                    is InterfaceModel -> {
                        val element = interfaces.remove(declaration.name)
                        if (element != null) listOf(element) else emptyList()
                    }
                    else -> listOf(declaration)
                }
            }
            .flatten()
            .map { declaration ->
                when (declaration) {
                    is InterfaceModel -> {
                        declaration.copy(
                                companionObject = declaration.companionObject.merge(declaration.name, modulesToBeMergedWithInterfaces)
                        )
                    }
                    is ClassModel -> {
                        declaration.copy(
                                companionObject = declaration.companionObject.merge(declaration.name, modulesToBeMergedWithClasses)
                        )
                    }
                    else -> declaration
                }
            }


    return copy(declarations = mergedDeclarations, sumbodules = resolvedSubmodules)
}