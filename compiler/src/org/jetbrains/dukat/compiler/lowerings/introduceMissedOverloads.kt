package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private data class NodeData<T>(
        val optionalArgs: MutableList<Int>,
        val names: List<String>,
        val methodNode: T
)

private typealias NodeDataRecord<T> = MutableMap<List<ParameterValueDeclaration>, NodeData<T>>
private typealias NodesDataMap<T> = MutableMap<String, NodeDataRecord<T>>

private fun MethodNode.process(nodesDataMap: NodesDataMap<MethodNode>) {
    val params = parameters
    val nonOptionalHead = params.takeWhile { paramDeclaration ->
        !paramDeclaration.optional
    }

    val headTypes = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.type }
    val headNames = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.name }

    val methodNodeRecord = nodesDataMap.getOrPut(name) { mutableMapOf() }
    val methodData = methodNodeRecord.getOrPut(headTypes) { NodeData(mutableListOf(), headNames, this) }
    methodData.optionalArgs.add(params.size - nonOptionalHead.size)
}


private fun FunctionNode.process(nodesDataMap: NodesDataMap<FunctionNode>) {
    val params = parameters
    val nonOptionalHead = params.takeWhile { paramDeclaration ->
        !paramDeclaration.optional
    }

    val headTypes = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.type }
    val headNames = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.name }

    val methodNodeRecord = nodesDataMap.getOrPut(name.translate()) { mutableMapOf() }
    val methodData = methodNodeRecord.getOrPut(headTypes) { NodeData(mutableListOf(), headNames, this) }
    methodData.optionalArgs.add(params.size - nonOptionalHead.size)
}

private fun InterfaceNode.createDataMap(): NodesDataMap<MethodNode> {
    val methodsData: NodesDataMap<MethodNode> = mutableMapOf()

    members.forEach { member ->
        if (member is MethodNode) {
            member.process(methodsData)
        }
    }

    return methodsData
}

private fun ClassNode.createDataMap(): NodesDataMap<MethodNode> {
    val methodsData: NodesDataMap<MethodNode> = mutableMapOf()

    members.forEach { member ->
        if (member is MethodNode) {
            member.process(methodsData)
        }
    }

    return methodsData
}

private fun DocumentRootNode.createDataMap(): NodesDataMap<FunctionNode> {
    val nodeDataMap: NodesDataMap<FunctionNode> = mutableMapOf()

    declarations.forEach { member ->
        if (member is FunctionNode) {
            member.process(nodeDataMap)
        }
    }

    return nodeDataMap
}



private fun <T> Map.Entry<String, NodeDataRecord<T>>.process(
        generatedMethods: MutableList<T>,
        paramsResolved: (node: T, params: List<ParameterDeclaration>) -> T
) {
    val optionalData = value

    optionalData.forEach { types, (argsCount, names, originalNode) ->

        val params = types.zip(names).map { (type, name) ->
            ParameterDeclaration(name, type, null, false, false)
        }

        val argsCountGrouped = argsCount.groupingBy { it }.eachCount()

        val hasUniqueArity = argsCountGrouped.values.any { it == 1 }
        val doesntNeedsOverload = hasUniqueArity || ( types.isEmpty() && argsCountGrouped.keys.contains(0))

        if (!doesntNeedsOverload) {
            generatedMethods.add(paramsResolved(originalNode, params))
        }
    }
}

private fun NodesDataMap<MethodNode>.generateMethods(): List<MethodNode> {
    val generatedMethods = mutableListOf<MethodNode>()
    forEach { methodDataRecord ->
        methodDataRecord.process(generatedMethods) { node, params -> node.copy(parameters = params) }
    }

    return generatedMethods
}

private fun NodesDataMap<FunctionNode>.generateFunctions(): List<FunctionNode> {
    val generatedFunctions = mutableListOf<FunctionNode>()
    forEach { functionDataRecord ->
        functionDataRecord.process(generatedFunctions) { node, params -> node.copy(parameters = params) }
    }

    return generatedFunctions
}

private class IntroduceMissedOverloads : ParameterValueLowering {

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        return declaration.copy(members = declaration.members + declaration.createDataMap().generateMethods())
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        return declaration.copy(members = declaration.members + declaration.createDataMap().generateMethods())
    }


    override fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        val nodesDataMap = documentRoot.createDataMap()

        return documentRoot.copy(
                declarations = lowerTopLevelDeclarations(documentRoot.declarations) + nodesDataMap.generateFunctions()
        )
    }
}

fun DocumentRootNode.introduceMissedOverloads(): DocumentRootNode {
    return IntroduceMissedOverloads().lowerDocumentRoot(this)
}

fun SourceSetNode.introduceMissedOverloads() = transform { it.introduceMissedOverloads() }