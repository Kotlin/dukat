package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.ParametersOwnerModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun List<ParameterModel>.paramPrefixes(): List<List<ParameterModel>> {
    val head = takeWhile { it.initializer == null }
    val tail = subList(head.size, size)

    return tail.indices.map { head + tail.subList(0, it) }
}

private fun <T : ParametersOwnerModel<ParameterModel>> unrollDefaults(params: List<ParameterModel>, copy: (List<ParameterModel>) -> T): List<T> {
    return params.paramPrefixes().map { copy(it) }
}

private fun <T : ParametersOwnerModel<ParameterModel>> resolveDefaults(params: List<ParameterModel>, copy: (List<ParameterModel>) -> T): List<T> {
    return if ((params.lastOrNull()?.initializer != null)) {
        unrollDefaults(params.dropLast(0), copy)
    } else {
        emptyList()
    }
}

private fun MemberModel.resolveDefaults(methodsMap: Map<NameEntity, Boolean>, hasManyConstructors: Boolean?): List<MemberModel> {
    return when (this) {
        is MethodModel -> {
            if (methodsMap[name] == true) {
                listOf(this) + resolveDefaults(parameters) { copy(parameters = it) }
            } else {
                listOf(this)
            }
        }
        is ConstructorModel -> {
            if (hasManyConstructors == true) {
                listOf(this) + resolveDefaults(parameters) { copy(parameters = it) }
            } else {
                listOf(this)
            }
        }
        else -> listOf(this)
    }
}

private fun ClassLikeModel.processMembers(): List<MemberModel> {
    val methodsData = mutableMapOf<NameEntity, Boolean>()
    var hasManyConstructors: Boolean? = null

    members.forEach {
        if (it is MethodModel) {
            val key = it.name
            if (methodsData[key] == null) {
                methodsData[key] = false
            } else if (methodsData[key] == false) {
                methodsData[key] = true
            }
        } else if (it is ConstructorModel) {
            if (hasManyConstructors == null) {
                hasManyConstructors = false
            } else if (hasManyConstructors == false) {
                hasManyConstructors = true
            }
        }
    }
    return members.flatMap { member ->
        member.resolveDefaults(methodsData, hasManyConstructors)
    }
}


private fun TopLevelModel.resolveDefaults(topLevelMap: Map<NameEntity, Boolean>): List<TopLevelModel> {
    return when (this) {
        is FunctionModel -> {
            if (topLevelMap[name] == true) {
                listOf(this) + resolveDefaults(parameters) { copy(parameters = it) }
            } else {
                listOf(this)
            }
        }
        else -> listOf(this)
    }
}

private fun ModuleModel.processTopLevelDeclarations(topLevelMap: Map<NameEntity, Boolean>): List<TopLevelModel> {
    return declarations.flatMap { declaration ->
        declaration.resolveDefaults(topLevelMap)
    }
}

private class ResolveOverloadResolutionAmbiguityLowering : ModelWithOwnerTypeLowering {
//    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
//        val model = ownerContext.node
//        return super.lowerInterfaceModel(ownerContext.copy(node = model.copy(members = model.processMembers())), parentModule)
//    }
//
//    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
//        val model = ownerContext.node
//        return super.lowerClassModel(ownerContext.copy(node = model.copy(members = model.processMembers())), parentModule)
//    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        val topLevelMap = mutableMapOf<NameEntity, Boolean>()
        moduleModel.declarations.forEach {
            if (it is FunctionModel) {
                val key = it.name
                if (topLevelMap[key] == null) {
                    topLevelMap[key] = false
                } else if(topLevelMap[key] == false) {
                    topLevelMap[key] = true
                }
            }
        }
        return super.lowerRoot(moduleModel.copy(declarations = moduleModel.processTopLevelDeclarations(topLevelMap)), ownerContext)
    }
}

class ResolveOverloadResolutionAmbiguity : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return ResolveOverloadResolutionAmbiguityLowering().lowerRoot(module, NodeOwner(module, null))
    }
}