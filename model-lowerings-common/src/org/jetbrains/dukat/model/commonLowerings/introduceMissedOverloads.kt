package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private data class ModelData<T>(
        val optionalArgs: MutableList<Int>,
        val names: List<String>,
        val methodNode: T
)

private typealias ModelDataRecord<T> = MutableMap<List<TypeModel>, ModelData<T>>
private typealias ModelsDataMap<T> = MutableMap<NameEntity, ModelDataRecord<T>>

private fun MethodModel.process(modelsDataMap: ModelsDataMap<MethodModel>) {
    val nonOptionalHead = parameters.takeWhile { paramDeclaration ->
        paramDeclaration.initializer == null
    }

    val headTypes = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.type }
    val headNames = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.name }

    val methodNodeRecord = modelsDataMap.getOrPut(name) { mutableMapOf() }
    val methodData = methodNodeRecord.getOrPut(headTypes) { ModelData(mutableListOf(), headNames, this) }
    methodData.optionalArgs.add(parameters.size - nonOptionalHead.size)
}


private fun FunctionModel.process(modelsDataMap: ModelsDataMap<FunctionModel>) {
    val nonOptionalHead = parameters.takeWhile { paramDeclaration ->
        paramDeclaration.initializer == null
    }

    val headTypes = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.type }
    val headNames = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.name }

    val methodNodeRecord = modelsDataMap.getOrPut(name) { mutableMapOf() }
    val methodData = methodNodeRecord.getOrPut(headTypes) { ModelData(mutableListOf(), headNames, this) }
    methodData.optionalArgs.add(parameters.size - nonOptionalHead.size)
}

private fun ClassLikeModel.createDataMap(): ModelsDataMap<MethodModel> {
    val methodsData: ModelsDataMap<MethodModel> = mutableMapOf()

    members.forEach { member ->
        if (member is MethodModel) {
            member.process(methodsData)
        }
    }

    return methodsData
}

private fun ModuleModel.createDataMap(): ModelsDataMap<FunctionModel> {
    val nodeDataMap: ModelsDataMap<FunctionModel> = mutableMapOf()

    declarations.forEach { member ->
        if (member is FunctionModel) {
            member.process(nodeDataMap)
        }
    }

    return nodeDataMap
}


private fun <T> Map.Entry<NameEntity, ModelDataRecord<T>>.process(
        generatedMethods: MutableList<T>,
        paramsResolved: (node: T, params: List<ParameterModel>) -> T
) {
    val optionalData = value

    optionalData.forEach { types, (argsCount, names, originalNode) ->

        val params = types.zip(names).map { (type, name) ->
            ParameterModel(name, type, null, false, null)
        }

        val argsCountGrouped = argsCount.groupingBy { it }.eachCount()

        val hasUniqueArity = argsCountGrouped.values.any { it == 1 }
        val doesntNeedsOverload = hasUniqueArity || (types.isEmpty() && argsCountGrouped.keys.contains(0))

        if (!doesntNeedsOverload) {
            generatedMethods.add(paramsResolved(originalNode, params))
        }
    }
}

private fun ModelsDataMap<MethodModel>.generateMethods(): List<MethodModel> {
    val generatedMethods = mutableListOf<MethodModel>()
    forEach { methodDataRecord ->
        methodDataRecord.process(generatedMethods) { node, params -> node.copy(parameters = params) }
    }

    return generatedMethods
}

private fun ModelsDataMap<FunctionModel>.generateFunctions(): List<FunctionModel> {
    val generatedFunctions = mutableListOf<FunctionModel>()
    forEach { functionDataRecord ->
        functionDataRecord.process(generatedFunctions) { node, params -> node.copy(parameters = params) }
    }

    return generatedFunctions
}

private class IntroduceMissedOverloadsTypeLowering : ModelWithOwnerTypeLowering {
    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val model = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = ownerContext.node.copy(
            members = model.members + model.createDataMap().generateMethods()
        )), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val model = ownerContext.node
        return super.lowerClassModel(ownerContext.copy(node = ownerContext.node.copy(
                members = model.members + model.createDataMap().generateMethods()
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