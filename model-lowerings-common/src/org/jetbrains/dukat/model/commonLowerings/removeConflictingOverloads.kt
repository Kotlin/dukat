package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap

fun TypeModel.normalize(context: ModelContext): TypeModel {
    return when (this) {
        is TypeValueModel -> {
            val unaliased = context.unalias(this)
            if (unaliased != this) {
                unaliased.normalize(context)
            } else {
                copy(metaDescription = null, params = params.map { it.copy(type = it.type.normalize(context)) }, value = fqName ?: value)
            }
        }
        is TypeParameterReferenceModel -> copy(metaDescription = null)
        is FunctionTypeModel -> copy(metaDescription = null, parameters = parameters.map { it.copy(type = it.type.normalize(context), name = null) })
        else -> this
    }
}

typealias CallableKey = Triple<NameEntity, List<TypeModel>, List<TypeModel>>

private class ConflictingOverloads(private val context: ModelContext) : TopLevelModelLowering {
    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val node = ownerContext.node.copy(members = ownerContext.node.resolveMembers())
        return super.lowerInterfaceModel(ownerContext.copy(node = node), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val node = ownerContext.node.copy(members = ownerContext.node.resolveMembers())
        return super.lowerClassModel(ownerContext.copy(node = node), parentModule)
    }


    private fun mergeTypeModels(a: TypeModel, b: TypeModel): TypeModel {
        return if ((a is TypeValueModel) && (b is TypeValueModel)) {
            val metaDescription = listOfNotNull(a.metaDescription, b.metaDescription).distinct().joinToString(" | ").ifEmpty { null }
            a.copy(metaDescription = metaDescription, params = mergeTypeParams(a.params, b.params))
        } else if ((a is FunctionTypeModel) && (b is FunctionTypeModel)) {
            a.copy(type = mergeTypeModels(a.type, b.type), parameters = mergeLambdaParams(a.parameters, b.parameters))
        } else {
            a
        }
    }

    private fun mergeTypeModelsAsReturn(a: TypeModel, b: TypeModel): TypeModel {
        return if ((a is TypeValueModel) && (b is TypeValueModel)) {
            if (a.normalize(context) == b.normalize(context)) {
                mergeTypeModels(a, b)
            } else {
                TypeValueModel(IdentifierEntity("dynamic"), listOf(), listOfNotNull(a.fqName?.rightMost(), b.fqName?.rightMost()).joinToString(" | ") { it.toString() }, null)
            }
        } else if ((a is FunctionTypeModel) && (b is FunctionTypeModel)) {
            if (a.normalize(context) == b.normalize(context)) {
                mergeTypeModels(a, b)
            } else {
                TypeValueModel(IdentifierEntity("dynamic"), listOf(), null, null)
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

    private fun mergeLambdaParams(a: List<LambdaParameterModel>, b: List<LambdaParameterModel>): List<LambdaParameterModel> {
        return a.zip(b).map { (paramA, paramB) ->
            paramA.copy(type = mergeTypeModels(paramA.type, paramB.type))
        }
    }

    private fun mergeParams(a: List<ParameterModel>, b: List<ParameterModel>): List<ParameterModel> {
        return a.zip(b).map { (paramA, paramB) ->
            paramA.copy(type = mergeTypeModels(paramA.type, paramB.type))
        }
    }

    private fun mergePropertyModels(a: PropertyModel, b: PropertyModel): PropertyModel {
        return a.copy(type = mergeTypeModelsAsReturn(a.type, b.type))
    }

    private fun mergeMethodModels(a: MethodModel, b: MethodModel): MethodModel {
        return a.copy(parameters = mergeParams(a.parameters, b.parameters), type = mergeTypeModelsAsReturn(a.type, b.type))
    }

    private fun mergeFunctionModels(a: FunctionModel, b: FunctionModel): FunctionModel {
        return a.copy(parameters = mergeParams(a.parameters, b.parameters), type = mergeTypeModelsAsReturn(a.type, b.type))
    }

    private fun ClassLikeModel.resolveMembers(): List<MemberModel> {
        val methodsKeyCache = mutableMapOf<MethodModel, CallableKey>()
        val propertiesKeyCache = mutableMapOf<PropertyModel, CallableKey>()

        val methodsBucket = members.filterIsInstance(MethodModel::class.java).groupBy { methodModel ->
            val key = methodModel.getKey()
            methodsKeyCache.put(methodModel, key)
            key
        }.toMutableMap()

        val propertiesBucket = members.filterIsInstance<PropertyModel>().groupBy { propertyModel ->
            val key = propertyModel.getKey()
            propertiesKeyCache[propertyModel] = key
            key
        }.toMutableMap()

        return members.mapNotNull { memberModel ->
            when (memberModel) {
                is MethodModel -> {
                    methodsBucket.remove(methodsKeyCache[memberModel])?.reduce { a, b -> mergeMethodModels(a, b) }
                }
                is PropertyModel -> {
                    propertiesBucket.remove(propertiesKeyCache[memberModel])?.reduce { a, b -> mergePropertyModels(a, b) }
                }
                else -> memberModel
            }
        }
    }

    private fun FunctionModel.getKey(): CallableKey {
        return Triple(name, parameters.map { it.type.normalize(context) }, typeParameters.map { it.type.normalize(context) })
    }

    private fun MethodModel.getKey(): CallableKey {
        return Triple(name, parameters.map { it.type.normalize(context) }, typeParameters.map { it.type.normalize(context) })
    }

    private fun PropertyModel.getKey(): CallableKey {
        return Triple(name, listOf(type.normalize(context)), typeParameters.map { it.type.normalize(context) })
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        val keyCache = mutableMapOf<FunctionModel, CallableKey>()

        val functionsBucket = moduleModel.declarations.filterIsInstance(FunctionModel::class.java).groupBy { functionModel ->
            val key = functionModel.getKey()
            keyCache.put(functionModel, key)
            key
        }.toMutableMap()

        val declarationsResolved = moduleModel.declarations.mapNotNull { topLevelModel ->
            when (topLevelModel) {
                is FunctionModel -> {
                    functionsBucket.remove(keyCache[topLevelModel])?.reduce { a, b -> mergeFunctionModels(a, b) }
                }
                else -> topLevelModel
            }
        }

        return moduleModel.copy(
            declarations = declarationsResolved.mapNotNull { lowerTopLevelModel(ownerContext.wrap(it), moduleModel) },
            submodules = moduleModel.submodules.map { lowerRoot(it, ownerContext.wrap(it)) }
        )
    }
}

class RemoveConflictingOverloads : ModelLowering {
    private lateinit var modelContext: ModelContext

    override fun lower(module: ModuleModel): ModuleModel {
        return ConflictingOverloads(modelContext).lowerRoot(module, NodeOwner(module, null))
    }

    override fun lower(source: SourceSetModel): SourceSetModel {
        modelContext = ModelContext(source)
        return super.lower(source)
    }
}