package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MergableNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.compiler.model.ROOT_CLASS_DECLARATION


private fun ModuleModel.canBeMerged(): Boolean {
    return declarations.any { declaration -> declaration is MemberDeclaration }
}

private fun VariableModel.convert(): MemberNode {
    return PropertyModel(
            name = name.translate(),
            type = type,
            typeParameters = emptyList(),
            static = false,
            override = false,
            getter = false,
            setter = false,
            open = false,
            definedExternally = true
    )
}

private fun FunctionModel.convert(): MemberNode {

    return MethodModel(
            name = name.translate(),
            parameters = parameters,
            type = type,
            typeParameters = typeParameters,
            static = false,
            override = false,
            operator = false,
            annotations = annotations,
            open = false,
            definedExternally = true
    )
}

private fun MergableNode.convert(): MemberNode {
    return when (this) {
        is FunctionModel -> convert()
        is VariableModel -> convert()
        else -> throw Error("can not convert unknown MergableNode ${this}")
    }
}

private fun CompanionObjectModel.merge(ownerName: String, modulesToBeMerged: Map<String, MutableList<ModuleModel>>): CompanionObjectModel {
    val members = members.toMutableList()
    modulesToBeMerged.getOrDefault(ownerName, mutableListOf()).forEach { module ->
        val submoduleDecls = module.declarations
                .filterIsInstance(MergableNode::class.java)
                .map { it.convert() }
        members.addAll(submoduleDecls)
    }

    return copy(members = members)
}

// TODO: duplication, think of separate place to have this (but please don't call it utils )))
private fun unquote(name: String): String {
    return name.replace("(?:^\"|\')|(?:\"|\'$)".toRegex(), "")
}


private fun collectModelsToBeMerged(submodules: List<ModuleModel>, context: Map<String, ClassLikeNode>, modulesToBeMerged: MutableMap<String, MutableList<ModuleModel>>): List<ModuleModel> {


    return submodules.map { subModule ->
        val moduleKey = unquote(subModule.shortName)
        if ((context.containsKey(moduleKey)) && (subModule.canBeMerged())) {
            val bucket = modulesToBeMerged.getOrPut(moduleKey) { mutableListOf() }
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

fun SourceSetModel.mergeClassLikesAndModuleDeclarations() = transform { it.mergeClassLikesAndModuleDeclarations() }