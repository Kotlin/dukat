package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.lowerings.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering


private fun Declaration.getKey(): String {
    return when (this) {
        is ClassNode -> uid
        is InterfaceNode -> uid
        is VariableNode -> uid
        is FunctionNode -> uid
        is ObjectNode -> name
        is EnumNode -> ""
        is DocumentRootNode -> ""
        is TypeAliasNode -> ""
        else -> throw Exception("unknown TopLevelNode ${this::class.simpleName}")
    }
}

private class RearrangeLowering() : NodeTypeLowering {
    val myReferences = mutableMapOf<String, MutableList<String>>()

    fun getReferences(): Map<String, List<String>> {
        return myReferences
    }

    private fun findTopLevelOwner(ownerContext: NodeOwner<*>): NodeOwner<out Declaration>? {
        ownerContext.getOwners().forEach { owner ->
            if (owner is NodeOwner<*>) {
                when (owner.node) {
                    is ClassNode -> return owner as NodeOwner<ClassNode>
                    is InterfaceNode -> return owner as NodeOwner<InterfaceNode>
                    is FunctionNode -> return owner as NodeOwner<FunctionNode>
                    is ObjectNode -> return owner as NodeOwner<ObjectNode>
                }
            }
        }

        return null
    }

    override fun lowerParameterValue(ownerContext: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {

        val declaration = ownerContext.node

        if (declaration is GeneratedInterfaceReferenceDeclaration) {
            findTopLevelOwner(ownerContext)?.let {
                myReferences.getOrPut(it.node.getKey()) { mutableListOf() }.add(declaration.name)
            }
        }

        return super.lowerParameterValue(ownerContext)
    }


}

private fun DocumentRootNode.generatedEntitiesMap(): Pair<MutableMap<String, InterfaceNode>, List<TopLevelDeclaration>> {
    val generatedDeclarations = mutableListOf<InterfaceNode>()
    val nonGeneratedDeclarations = declarations.mapNotNull { declaration ->
        if ((declaration is InterfaceNode) && (declaration.generated)) {
            generatedDeclarations.add(declaration)
            null
        } else {
            declaration
        }
    }

    val generatedDeclarationsMap = mutableMapOf<String, InterfaceNode>()
    generatedDeclarations.map { declaration ->
        generatedDeclarationsMap.put(declaration.name, declaration)
    }

    return Pair(generatedDeclarationsMap, nonGeneratedDeclarations)
}

private fun TopLevelDeclaration.generateEntites(references: Map<String, List<String>>, generatedDeclarationsMap: MutableMap<String, InterfaceNode>): List<TopLevelDeclaration> {
    val genRefs = references.getOrDefault(getKey(), emptyList())
    val genEntities = genRefs.mapNotNull {
        val interfaceNode = generatedDeclarationsMap.get(it)
        if (interfaceNode != null) {
            generatedDeclarationsMap.remove(it)
            interfaceNode.generateEntites(references, generatedDeclarationsMap) + listOf(interfaceNode)
        } else {
            null
        }
    }.flatten()
    return genEntities
}

fun DocumentRootNode.rearrangeGeneratedEntities(): DocumentRootNode {
    val rearrangeLowering = RearrangeLowering()
    rearrangeLowering.lowerRoot(this, NodeOwner(this, null))
    val references = rearrangeLowering.getReferences()

    val (generatedDeclarationsMap, nonGeneratedDeclarations) = generatedEntitiesMap()

    val declarationsRearranged = nonGeneratedDeclarations.flatMap { declaration ->
        declaration.generateEntites(references, generatedDeclarationsMap) + listOf(declaration)
    }

    return copy(declarations = declarationsRearranged)
}

fun SourceSetNode.rearrangeGeneratedEntities() = transform { it.rearrangeGeneratedEntities() }