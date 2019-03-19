package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


private fun ParameterValueDeclaration.debugTranslate(): String {
    return when (this) {
        is TypeNode -> value.toString()
        is FunctionTypeNode -> "FunctionTypeNode"
        else -> throw Exception("unknown ParameterValueDeclaration ${this}")
    }
}

private data class MethodData(
        val optionalArgs: MutableList<Int>,
        val names: List<String>,
        val methodNode: MethodNode
)

private typealias MethodDataRecord = MutableMap<List<ParameterValueDeclaration>, MethodData>
private typealias MethodsDataMap = MutableMap<String, MethodDataRecord>

private fun MethodNode.process(methodsDataMap: MethodsDataMap) {
    val params = parameters
    val nonOptionalHead = params.takeWhile { paramDeclaration ->
        !paramDeclaration.optional
    }

    val headTypes = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.type }
    val headNames = nonOptionalHead.map { parameterDeclaration -> parameterDeclaration.name }

    val methodNodeRecord = methodsDataMap.getOrPut(name) { mutableMapOf() }
    val methodData = methodNodeRecord.getOrPut(headTypes) { MethodData(mutableListOf(), headNames, this) }
    methodData.optionalArgs.add(params.size - nonOptionalHead.size)
}

private fun InterfaceNode.collectWeights(): MethodsDataMap {
    val methodsData: MethodsDataMap = mutableMapOf()

    members.forEach { member ->
        if (member is MethodNode) {
            member.process(methodsData)
        }
    }

    return methodsData
}

private fun ClassNode.collectWeights(): MethodsDataMap {
    val methodsData: MethodsDataMap = mutableMapOf()

    members.forEach { member ->
        if (member is MethodNode) {
            member.process(methodsData)
        }
    }

    return methodsData
}


private fun Map.Entry<String, MethodDataRecord>.process(generatedMethods: MutableList<MethodNode>) {
    val optionalData = value

    optionalData.forEach { types, (argsCount, names, originalMethodNode) ->

        val params = types.zip(names).map { (type, name) ->
            ParameterDeclaration(name, type, null, false, false)
        }

        val argsCountGrouped = argsCount.groupingBy { it }.eachCount()

        val haseUniqueArity = argsCountGrouped.values.any { it == 1 }
        val doesntNeedsOverload = haseUniqueArity || ( types.isEmpty() && argsCountGrouped.keys.contains(0))

        if (!doesntNeedsOverload) {
            generatedMethods.add(
                    originalMethodNode.copy(parameters = params)
            )
        }
    }
}

private fun MethodsDataMap.generateMethods(): List<MethodNode> {
    val generatedMethods = mutableListOf<MethodNode>()
    this.forEach { weightRecord ->
        weightRecord.process(generatedMethods)
    }

    return generatedMethods
}

private class IntroduceMissedOverloads : ParameterValueLowering {

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        return declaration.copy(members = declaration.members + declaration.collectWeights().generateMethods())
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        return declaration.copy(members = declaration.members + declaration.collectWeights().generateMethods())
    }

}

fun DocumentRootNode.introduceMissedOverloads(): DocumentRootNode {
    return IntroduceMissedOverloads().lowerDocumentRoot(this)
}

fun SourceSetNode.introduceMissedOverloads() = transform { it.introduceMissedOverloads() }