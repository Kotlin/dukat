package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.commonLowerings.merge.processing.fetchClassLikes
import org.jetbrains.dukat.commonLowerings.merge.processing.fetchModules

private fun VariableModel.convert(): MemberModel {
    return PropertyModel(
            name = name,
            type = type,
            typeParameters = emptyList(),
            static = false,
            override = null,
            immutable = immutable,
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
            override = null,
            operator = false,
            annotations = annotations,
            open = false
    )
}

private fun TopLevelModel.convert(): MemberModel? {
    return when (this) {
        is FunctionModel -> convert()
        is VariableModel -> convert()
        else -> null
    }
}

private fun ObjectModel?.merge(module: ModuleModel): ObjectModel? {
    val staticMembers = module.declarations
            .mapNotNull { it.convert() }

    return this?.copy(members = members + staticMembers) ?: if (staticMembers.isEmpty()) {
        null
    } else {
        ObjectModel(IdentifierEntity(""), staticMembers, listOf(), VisibilityModifierModel.DEFAULT, null)
    }
}

private fun ModuleModel.mergeClassLikesAndModuleDeclarations(classLikes: Map<NameEntity, MergeClassLikeData>): ModuleModel {
    val extractedTypeAliases = mutableListOf<TypeAliasModel>()

    val declarationLowered = declarations.map { declaration ->
        classLikes[name.appendLeft(declaration.name)]?.let {
            extractedTypeAliases.addAll(it.extractedTypeAliases)
            it.model
        } ?: declaration
    }
    val submodulesLowered = submodules.filter {
        !classLikes.containsKey(it.mergeClassLikesAndModuleDeclarations(classLikes).name)
    }.map { it.mergeClassLikesAndModuleDeclarations(classLikes) }

    return copy(declarations = extractedTypeAliases + declarationLowered, submodules = submodulesLowered)
}

private operator fun ClassLikeModel.plus(b: ModuleModel): ClassLikeModel {
    return when (this) {
        is InterfaceModel -> this + b
        is ClassModel -> this + b
        else -> this
    }
}

private fun ModuleModel.fetchDeclarations(): List<ClassLikeModel> {
    return declarations.filterIsInstance(ClassLikeModel::class.java).map {
        when (it) {
            is ClassModel -> it.copy(external = false)
            is InterfaceModel -> it.copy(external = false)
            else -> it
        }
    }
}

private operator fun InterfaceModel.plus(moduleModel: ModuleModel): InterfaceModel {
    return copy(
            members = members + moduleModel.fetchDeclarations(),
            companionObject = companionObject.merge(moduleModel)
    )
}

private operator fun ClassModel.plus(moduleModel: ModuleModel): ClassModel {
    return copy(
            members = members + moduleModel.fetchDeclarations(),
            companionObject = companionObject.merge(moduleModel)
    )
}

internal data class MergeClassLikeData(val model: ClassLikeModel, val extractedTypeAliases: List<TypeAliasModel>)

fun SourceSetModel.mergeClassLikesAndModuleDeclarations(): SourceSetModel {
    val modules = sources
            .flatMap { source -> source.root.fetchModules() }
            .groupBy { it.ownerName }
            .mapValues { (_, v) -> v[0].model }

    val classLikes = sources
            .flatMap { source -> source.root.fetchClassLikes() }
            .groupBy { it.ownerName }
            .filterKeys { name -> modules.containsKey(name) }
            .mapValues { (name, v) ->
                val moduleToMerge = modules[name]!!
                MergeClassLikeData(
                        v[0].model + moduleToMerge,
                        moduleToMerge.declarations.filterIsInstance(TypeAliasModel::class.java)
                )
            }

    return copy(sources = sources.map { source -> source.copy(root = source.root.mergeClassLikesAndModuleDeclarations(classLikes)) })
}