package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun TypeModel.withoutMeta(): TypeModel {
    return when (this) {
        is TypeValueModel -> copy(metaDescription = null, params = params.map { param -> param.copy(type = param.type.withoutMeta()) })
        else -> this
    }
}

private class ConflictingOverloads : TopLevelModelLowering {
    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val node = ownerContext.node.copy(members = ownerContext.node.resolveMembers())
        return super.lowerInterfaceModel(ownerContext.copy(node = node), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val node = ownerContext.node.copy(members = ownerContext.node.resolveMembers())
        return super.lowerClassModel(ownerContext.copy(node = node), parentModule)
    }
}

private fun mergeTypeModels(a: TypeModel, b: TypeModel): TypeModel {
    return if ((a is TypeValueModel) && (b is TypeValueModel)) {
        val metaDescription = listOfNotNull(a.metaDescription, b.metaDescription).distinct().let {
            if (it.isEmpty()) {
                null
            } else {
                it.joinToString(" | ")
            }
        }
        a.copy(metaDescription = metaDescription, params = mergeTypeParams(a.params, b.params))
    } else {
        a
    }
}

private fun mergeTypeModelsAsReturn(a: TypeModel, b: TypeModel): TypeModel {
    return if ((a is TypeValueModel) && (b is TypeValueModel)) {
        if (a.withoutMeta() == b.withoutMeta()) {
            mergeTypeModels(a, b)
        } else {
            TypeValueModel(IdentifierEntity("dynamic"), listOf(), listOfNotNull(a.fqName?.rightMost(), b.fqName?.rightMost()).joinToString(" | ") { it.toString() }, null)
        }
    } else {
        TypeValueModel(IdentifierEntity("dynamic"), listOf(), null, null)
    }
}

private fun mergeTypeParams(a: List<TypeParameterModel>, b: List<TypeParameterModel>): List<TypeParameterModel> {
    return a.zip(b).map { (paramA, paramB) ->
        paramA.copy(type = mergeTypeModels(paramA.type, paramB.type))
    }
}

private fun mergeParams(a: List<ParameterModel>, b: List<ParameterModel>): List<ParameterModel> {
    return a.zip(b).map { (paramA, paramB) ->
        paramA.copy(type = mergeTypeModels(paramA.type, paramB.type))
    }
}

private fun mergeMethodModels(a: MethodModel, b: MethodModel): MethodModel {
    return a.copy(parameters = mergeParams(a.parameters, b.parameters), type = mergeTypeModelsAsReturn(a.type, b.type))
}

private fun mergeFunctionModels(a: FunctionModel, b: FunctionModel): FunctionModel {
    return a.copy(parameters = mergeParams(a.parameters, b.parameters), type = mergeTypeModelsAsReturn(a.type, b.type))
}

typealias CallableKey = Triple<NameEntity, List<TypeModel>, List<TypeModel>>

private fun ClassLikeModel.resolveMembers(): List<MemberModel> {
    val keyCache = mutableMapOf<MethodModel, CallableKey>()

    val methodsBucket = members.filterIsInstance(MethodModel::class.java).groupBy { methodModel ->
        val key = methodModel.getKey()
        keyCache.put(methodModel, key)
        key
    }.toMutableMap()

    return members.mapNotNull { memberModel ->
        when (memberModel) {
            is MethodModel -> {
                methodsBucket.remove(keyCache[memberModel])?.reduce { a, b -> mergeMethodModels(a, b) }
            }
            else -> memberModel
        }
    }
}

private fun FunctionModel.getKey(): CallableKey {
    return Triple(name, parameters.map { it.type.withoutMeta() }, typeParameters.map { it.type.withoutMeta() })
}

private fun MethodModel.getKey(): CallableKey {
    return Triple(name, parameters.map { it.type.withoutMeta() }, typeParameters.map { it.type.withoutMeta() })
}

class RemoveConflictingOverloads : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        val keyCache = mutableMapOf<FunctionModel, CallableKey>()

        val functionsBucket = module.declarations.filterIsInstance(FunctionModel::class.java).groupBy { functionModel ->
            val key = functionModel.getKey()
            keyCache.put(functionModel, key)
            key
        }.toMutableMap()

        val declarationsResolved = module.declarations.mapNotNull { topLevelModel ->
            when (topLevelModel) {
                is FunctionModel -> {
                    functionsBucket.remove(keyCache[topLevelModel])?.reduce { a, b -> mergeFunctionModels(a, b) }
                }
                else -> topLevelModel
            }
        }

        val moduleResolved = module.copy(declarations = declarationsResolved)
        return ConflictingOverloads().lowerRoot(moduleResolved, NodeOwner(moduleResolved, null))
    }
}