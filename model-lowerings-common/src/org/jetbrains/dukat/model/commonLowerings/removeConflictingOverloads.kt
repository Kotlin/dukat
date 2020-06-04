package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun TypeModel.withoutMeta(): TypeModel {
    return when (this) {
        is TypeValueModel -> copy(metaDescription = null)
        else -> this
    }
}

private fun ParameterModel.withoutMeta(): ParameterModel {
    return copy(type = type.withoutMeta())
}

private fun MemberModel.normalize(): MemberModel {
    return when (this) {
        is MethodModel -> copy(
                parameters = parameters.map { it.withoutMeta() },
                override = null
        )
        else -> this
    }
}

private fun filterOutConflictingOverloads(members: List<MemberModel>): List<MemberModel> {
    return members.groupBy { it.normalize() }.map { (_, bucketMembers) ->
        if (bucketMembers.size > 1) {
            bucketMembers.first().normalize()
        } else {
            bucketMembers.first()
        }
    }
}

private class ConflictingOverloads : TopLevelModelLowering {

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel? {
        val node = ownerContext.node.copy(members = filterOutConflictingOverloads(ownerContext.node.members))
        return super.lowerInterfaceModel(ownerContext.copy(node = node), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel? {
        val node = ownerContext.node.copy(members = filterOutConflictingOverloads(ownerContext.node.members))
        return super.lowerClassModel(ownerContext.copy(node = node), parentModule)
    }
}

private fun mergeTypeModels(a: TypeModel, b: TypeModel): TypeModel {
    return if ((a is TypeValueModel) && (b is TypeValueModel)) {
        a.copy(metaDescription = listOfNotNull(a.metaDescription, b.metaDescription).joinToString(" | "))
    } else {
        a
    }
}

private fun mergeTypeModelsAsReturn(a: TypeModel, b: TypeModel): TypeModel {
    return if ((a is TypeValueModel) && (b is TypeValueModel)) {
        a.copy(metaDescription = listOfNotNull(a.metaDescription, b.metaDescription).joinToString(" | "))
        if (a.withoutMeta() == b.withoutMeta()) {
            mergeTypeModels(a, b)
        } else {
            TypeValueModel(IdentifierEntity("dynamic"), listOf(), listOfNotNull(a.fqName?.rightMost(), b.fqName?.rightMost()).joinToString(" | ") { it.toString() }, null)
        }
    } else {
        TypeValueModel(IdentifierEntity("dynamic"), listOf(), null, null)
    }
}


private fun mergeFunctionModels(a: FunctionModel, b: FunctionModel): FunctionModel {
    val paramsMerged = a.parameters.zip(b.parameters).map { (paramA, paramB) ->
        paramA.copy(type = mergeTypeModels(paramA.type, paramB.type))
    }

    return a.copy(parameters = paramsMerged, type = mergeTypeModelsAsReturn(a.type, b.type))
}

typealias FunctionModelKey = Triple<NameEntity, List<TypeModel>, List<TypeModel>>

private fun FunctionModel.getKey(): FunctionModelKey {
    return Triple(name, parameters.map { it.type.withoutMeta() }, typeParameters.map { it.type.withoutMeta() })
}

class RemoveConflictingOverloads : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        val keyCache = mutableMapOf<FunctionModel, FunctionModelKey>()

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