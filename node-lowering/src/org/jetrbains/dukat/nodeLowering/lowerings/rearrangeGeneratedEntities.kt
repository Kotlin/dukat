package org.jetrbains.dukat.nodeLowering.lowerings

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
import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering


private fun Entity.getKey(): String {
    return when (this) {
        is ClassNode -> uid
        is InterfaceNode -> uid
        is VariableNode -> uid
        is FunctionNode -> uid
        is ObjectNode -> ""
        is TypeAliasNode -> uid
        is EnumNode -> ""
        is DocumentRootNode -> ""
        else -> raiseConcern("unknown TopLevelNode ${this::class.simpleName}") { "" }
    }
}

private class RearrangeLowering() : NodeWithOwnerTypeLowering {
    val myReferences = mutableMapOf<String, MutableList<NameEntity>>()

    fun getReferences(): Map<String, List<NameEntity>> {
        return myReferences
    }

    private fun findTopLevelOwner(ownerContext: NodeOwner<*>): NodeOwner<out Entity>? {
        ownerContext.getOwners().forEach { owner ->
            if (owner is NodeOwner<*>) {
                val node = owner.node
                when (node) {
                    is ClassNode -> return owner as NodeOwner<ClassNode>
                    is InterfaceNode -> return owner as NodeOwner<InterfaceNode>
                    is FunctionNode -> return owner as NodeOwner<FunctionNode>
                    is ObjectNode -> return owner as NodeOwner<ObjectNode>
                    is VariableNode -> return owner as NodeOwner<VariableNode>
                    is TypeAliasNode -> if (node.canBeTranslated) {
                        return owner as NodeOwner<TypeAliasNode>
                    } else null
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

private fun DocumentRootNode.generatedEntitiesMap(): Pair<MutableMap<NameEntity, InterfaceNode>, List<TopLevelEntity>> {
    val generatedDeclarations = mutableListOf<InterfaceNode>()
    val nonGeneratedDeclarations = declarations.mapNotNull { declaration ->
        if ((declaration is InterfaceNode) && (declaration.generated)) {
            generatedDeclarations.add(declaration)
            null
        } else {
            declaration
        }
    }

    val generatedDeclarationsMap = mutableMapOf<NameEntity, InterfaceNode>()
    generatedDeclarations.map { declaration ->
        generatedDeclarationsMap.put(declaration.name, declaration)
    }

    return Pair(generatedDeclarationsMap, nonGeneratedDeclarations)
}

private fun TopLevelEntity.generateEntites(references: Map<String, List<NameEntity>>, generatedDeclarationsMap: MutableMap<NameEntity, InterfaceNode>): List<TopLevelEntity> {
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