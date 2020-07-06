package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.NamedCallableModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private data class ModelData<T : NamedCallableModel>(
        val optionalArgs: MutableList<Int>,
        val methodNode: T
)

private typealias ModelDataRecord<T> = MutableMap<List<ParameterModel>, ModelData<T>>
private typealias ModelsDataMap<T> = Map<NameEntity, ModelDataRecord<T>>

private fun <CALLABLE_MODEL : NamedCallableModel> CALLABLE_MODEL.process(modelsDataMap: MutableMap<NameEntity, ModelDataRecord<CALLABLE_MODEL>>) {
    val nonOptionalHead = parameters.takeWhile { paramDeclaration ->
        paramDeclaration.initializer == null
    }

    val methodNodeRecord = modelsDataMap.getOrPut(name) { mutableMapOf() }

    val methodData = methodNodeRecord.getOrPut(
            nonOptionalHead.map { ParameterModel(it.name, it.type, null, false, null) }
    ) {
        ModelData(mutableListOf(), this)
    }

    methodData.optionalArgs.add(parameters.size - nonOptionalHead.size)
}


private fun ClassLikeModel.createDataMap(): ModelsDataMap<MethodModel> {
    val res = mutableMapOf<NameEntity, ModelDataRecord<MethodModel>>()

    members.forEach { member ->
        if (member is MethodModel) {
            member.process(res)
        }
    }

    return res
}

private fun ModuleModel.createDataMap(): ModelsDataMap<FunctionModel> {
    val res = mutableMapOf<NameEntity, ModelDataRecord<FunctionModel>>()

    declarations.forEach { member ->
        if (member is FunctionModel) {
            member.process(res)
        }
    }

    return res
}

private fun <CALLABLE_MODEL : NamedCallableModel> ModelDataRecord<CALLABLE_MODEL>.process(
        paramsResolved: (node: CALLABLE_MODEL, params: List<ParameterModel>) -> CALLABLE_MODEL
): List<CALLABLE_MODEL> {
    return mapNotNull { (types, modelData) ->
        val argsCountGrouped = modelData.optionalArgs.groupingBy { it }.eachCount()

        val hasUniqueArity = argsCountGrouped.values.any { it == 1 }
        val doesntNeedOverload = hasUniqueArity || (types.isEmpty() && argsCountGrouped.keys.contains(0))

        if (!doesntNeedOverload) {
            paramsResolved(modelData.methodNode, types)
        } else {
            null
        }
    }
}

private fun ModelsDataMap<MethodModel>.generateMethods(): List<MethodModel> {
    return flatMap { (_, methodData) ->
        methodData.process { node, params -> node.copy(parameters = params) }
    }
}

private fun ModelsDataMap<FunctionModel>.generateFunctions(): List<FunctionModel> {
    return flatMap { (_, functionData) ->
        functionData.process { node, params -> node.copy(parameters = params) }
    }
}

private fun ClassLikeModel.withGeneratedMembers(): List<MemberModel> {
    return members + createDataMap().generateMethods()
}

private class IntroduceMissedOverloadsTypeLowering : ModelWithOwnerTypeLowering {

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val model = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = model.copy(
                members = model.withGeneratedMembers()
        )), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val model = ownerContext.node
        return super.lowerClassModel(ownerContext.copy(node = model.copy(
                members = model.withGeneratedMembers()
        )), parentModule)
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        val nodesDataMap = moduleModel.createDataMap()
        return super.lowerRoot(moduleModel.copy(
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext, moduleModel) + nodesDataMap.generateFunctions()
        ), ownerContext)
    }
}

class IntroduceMissedOverloads : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return IntroduceMissedOverloadsTypeLowering().lowerRoot(module, NodeOwner(module, null))
    }
}