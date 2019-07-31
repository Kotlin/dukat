package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.unquote
import org.jetbrains.dukat.astModel.*


private fun ModuleModel.canBeMerged(): Boolean {
    return declarations.any { declaration -> declaration is MemberEntity }
}

private fun VariableModel.convert(): MemberModel {
    return PropertyModel(
            name = name,
            type = type,
            typeParameters = emptyList(),
            static = false,
            override = false,
            getter = false,
            setter = false,
            open = false
    )
}

private fun FunctionModel.convert(): MemberModel {

    return MethodModel(
            name = name,
            parameters = parameters,
            type = type,
            typeParameters = typeParameters,
            static = false,
            override = false,
            operator = false,
            annotations = annotations,
            open = false
    )
}

private fun MergeableModel.convert(): MemberModel {
    return when (this) {
        is FunctionModel -> convert()
        is VariableModel -> convert()
        else -> throw Error("can not convert unknown MergableNode ${this}")
    }
}

private fun CompanionObjectModel?.merge(ownerName: NameEntity, modulesToBeMerged: Map<NameEntity, MutableList<ModuleModel>>): CompanionObjectModel? {
    val members = this?.members.orEmpty().toMutableList()
    modulesToBeMerged.getOrDefault(ownerName, mutableListOf()).forEach { module ->
        val submoduleDecls = module.declarations
                .filterIsInstance(MergeableModel::class.java)
                .map { it.convert() }
        members.addAll(submoduleDecls)
    }

    return this?.copy(members = members) ?: if (members.isEmpty()) {
        null
    } else {
        CompanionObjectModel("", members, listOf())
    }
}

private fun collectModelsToBeMerged(submodules: List<ModuleModel>, context: Map<NameEntity, ClassLikeModel>, modulesToBeMerged: MutableMap<NameEntity, MutableList<ModuleModel>>): List<ModuleModel> {
    return submodules.map { subModule ->
        val moduleKey = subModule.shortName.unquote()
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
            companionObject = companionObject.mergeWith(interfaceModel.companionObject)
    )
}


fun ModuleModel.mergeClassLikesAndModuleDeclarations(): ModuleModel {
    val interfacesInBucket = mutableMapOf<NameEntity, MutableList<InterfaceModel>>()

    val classes = mutableMapOf<NameEntity, ClassModel>()

    declarations.forEach { declaration ->
        if (declaration is InterfaceModel) {
            interfacesInBucket.getOrPut(declaration.name) { mutableListOf() }.add(declaration)
        } else if (declaration is ClassModel) {
            classes[declaration.name] = declaration
        }
    }


    val interfaces = interfacesInBucket.mapValues { entry -> entry.value.reduceRight { interfaceModel, acc -> interfaceModel.merge(acc) } }.toMutableMap()

    val modulesToBeMergedWithInterfaces = mutableMapOf<NameEntity, MutableList<ModuleModel>>()
    val modulesToBeMergedWithClasses = mutableMapOf<NameEntity, MutableList<ModuleModel>>()

    var resolvedSubmodules = collectModelsToBeMerged(submodules, interfaces, modulesToBeMergedWithInterfaces)
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


    return copy(declarations = mergedDeclarations, submodules = resolvedSubmodules)
}

fun SourceSetModel.mergeClassLikesAndModuleDeclarations() = transform { it.mergeClassLikesAndModuleDeclarations() }