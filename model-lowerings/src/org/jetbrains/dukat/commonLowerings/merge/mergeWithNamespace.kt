package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.VariableModel

private fun ModuleModel.visit(visitor: (ModuleModel) -> Unit) {
    visitor(this)
    submodules.forEach { submodule -> submodule.visit(visitor) }
}

private fun ModuleModel.visitTopLevelNode(visitor: (TopLevelModel, ModuleModel) -> Unit) {
    visitor(this, this)
    declarations.forEach { declaration -> visitor(declaration, this) }
    submodules.forEach { submodule -> submodule.visitTopLevelNode(visitor) }
}

private fun ModuleModel.transformTopLevelNode(visitor: (TopLevelModel) -> TopLevelModel?): TopLevelModel? {
    val moduleResolved = visitor(this)
    return if (moduleResolved is ModuleModel) {
        return moduleResolved.copy(
                declarations = declarations.mapNotNull { declaration -> visitor(declaration) },
                submodules = submodules.mapNotNull { submodule -> (submodule.transformTopLevelNode(visitor) as ModuleModel?) }
        )
    } else null
}

private fun VariableModel.convertToPropertyModel(): PropertyModel {
    return PropertyModel(
            name = name,
            type = type,
            typeParameters = typeParameters,
            static = false,
            override = false,
            getter = false,
            setter = false,
            open = false
    )
}

private fun FunctionModel.convertToMethodModel(): MethodModel {
    return MethodModel(
            name = name,
            parameters = parameters,
            type = type,
            typeParameters = typeParameters,
            static = false,
            override = false,
            operator = false,
            annotations = emptyList(),
            open = false
    )
}

private fun ModuleModel.topLevelAsStaticProperties(): List<MemberModel> {
    return declarations.mapNotNull {
        when (it) {
            is VariableModel -> it.convertToPropertyModel()
            is FunctionModel -> it.convertToMethodModel()
            else -> null
        }
    }
}

private fun ClassLikeModel.merge(module: ModuleModel): ClassLikeModel {
    // TODO: investigate whethere there are casese when we have submodules
    val mergedMembers =
            module.declarations.filterIsInstance(MemberModel::class.java)
                    .map {
                        when (it) {
                            is InterfaceModel -> it.copy(external = false)
                            is ClassModel -> it.copy(external = false)
                            else -> it
                        }
                    }
    val staticProperties = module.topLevelAsStaticProperties()
    return when (this) {
        is InterfaceModel -> copy(
                members = members + mergedMembers,
                companionObject = if (staticProperties.isNotEmpty()) {
                    companionObject?.copy(
                            members = companionObject!!.members + staticProperties)
                            ?: CompanionObjectModel(
                                    "",
                                    staticProperties,
                                    listOf()
                            )
                } else {
                    companionObject
                }
        )
        is ClassModel -> {
            copy(
                    members = members + mergedMembers,
                    companionObject = if (staticProperties.isNotEmpty()) {
                        companionObject?.copy(
                                members = companionObject!!.members + staticProperties)
                                ?: CompanionObjectModel(
                                        "",
                                        staticProperties,
                                        listOf()
                                )
                    } else {
                        companionObject
                    }
            )
        }
        else -> this
    }
}

private fun ModuleModel.mergeWithNameSpace(
        modulesToMerge: Map<ClassLikeModel, ModuleModel>,
        modulesToSkip: Set<ModuleModel>
): ModuleModel {
    return transformTopLevelNode { topLevelNode ->
        modulesToMerge[topLevelNode]?.let { moduleToMerge ->
            (topLevelNode as ClassLikeModel).merge(moduleToMerge)
        } ?: if (modulesToSkip.contains(topLevelNode)) {
            null
        } else topLevelNode
    } as ModuleModel
}

fun SourceSetModel.mergeWithNameSpace(): SourceSetModel {
    val moduleMap = mutableMapOf<Pair<NameEntity, NameEntity>, ModuleModel>()
    val modulesToMerge = mutableMapOf<ClassLikeModel, ModuleModel>()
    val modulesToSkip = mutableSetOf<ModuleModel>()

    sources.forEach { source ->
        val rootModule = source.root
        rootModule.visit { module ->
            module.submodules.forEach { submodule ->
                moduleMap[Pair(rootModule.name, submodule.shortName)] = submodule
            }
        }
    }

    sources.forEach { source ->
        source.root.visitTopLevelNode { declaration, module ->
            val isMergeable = (declaration is ClassLikeModel)

            if (isMergeable) {
                moduleMap[Pair(module.name, declaration.name)]?.let { moduleToMerge ->
                    //TODO: to introduce better check here
                    if (!moduleToMerge.annotations.any { it.name == "file:JsModule" }) {
                        modulesToMerge[declaration as ClassLikeModel] = moduleToMerge
                        modulesToSkip.add(moduleToMerge)
                    }
                }
            }
        }
    }


    return copy(sources = sources.map { source -> source.copy(root = source.root.mergeWithNameSpace(modulesToMerge, modulesToSkip)) })
}