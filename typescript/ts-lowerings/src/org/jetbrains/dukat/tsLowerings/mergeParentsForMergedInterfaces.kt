package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

private class MergeParentsLowerer(private val definitionsMap: Map<List<DefinitionInfoDeclaration>, List<HeritageClauseDeclaration>>) : DeclarationTypeLowering {
    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): InterfaceDeclaration? {
        val declarationResolved = definitionsMap[declaration.definitionsInfo]?.let { parentEntities ->
            declaration.copy(parentEntities = parentEntities)
        } ?: declaration
        return super.lowerInterfaceDeclaration(declarationResolved, owner)
    }
}

private fun ModuleDeclaration.buildInterfaceMap(map: MutableMap<String, InterfaceDeclaration>) {
    declarations.forEach {
        when (it) {
            is InterfaceDeclaration -> map[it.uid] = it
            is ModuleDeclaration -> it.buildInterfaceMap(map)
        }
    }
}

private fun SourceSetDeclaration.buildInterfaceMap(): Map<String, InterfaceDeclaration> {
    val interfaceMap = mutableMapOf<String, InterfaceDeclaration>()
    sources.forEach { it.root.buildInterfaceMap(interfaceMap) }
    return interfaceMap
}

private fun mergeParentEntities(parentEntitiesA: List<HeritageClauseDeclaration>, parentEntitiesB: List<HeritageClauseDeclaration>): List<HeritageClauseDeclaration> {
    val parentSet = parentEntitiesA.toSet()
    return parentEntitiesA + parentEntitiesB.filter { parentEntity -> !parentSet.contains(parentEntity) }
}

private fun ModuleDeclaration.buildDefinitionsParentsMap(interfaceMap: Map<String, InterfaceDeclaration>, map: MutableMap<List<DefinitionInfoDeclaration>, List<HeritageClauseDeclaration>>) {
    declarations.forEach {
        when (it) {
            is InterfaceDeclaration -> {
                val parents = it.definitionsInfo.map { definition ->
                    interfaceMap[definition.uid]?.parentEntities ?: emptyList()
                }
                map.putIfAbsent(it.definitionsInfo, parents.fold(emptyList(), ::mergeParentEntities))
            }
            is ModuleDeclaration -> it.buildDefinitionsParentsMap(interfaceMap, map)
        }
    }
}

private fun SourceSetDeclaration.buildDefinitionsParentsMap(interfaceMap: Map<String, InterfaceDeclaration>): Map<List<DefinitionInfoDeclaration>, List<HeritageClauseDeclaration>> {
    val definitionsMap = mutableMapOf<List<DefinitionInfoDeclaration>, List<HeritageClauseDeclaration>>()
    sources.forEach { it.root.buildDefinitionsParentsMap(interfaceMap, definitionsMap) }
    return definitionsMap
}

private fun SourceSetDeclaration.mergeParentsForMergedInterfaces(): SourceSetDeclaration {
    val interfaceMap = buildInterfaceMap()
    val definitionsMap = buildDefinitionsParentsMap(interfaceMap)
    return copy(sources = sources.map { it.copy(root = MergeParentsLowerer(definitionsMap).lowerDocumentRoot(it.root)) })
}

class MergeParentsForMergedInterfaces(): TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.mergeParentsForMergedInterfaces()
    }
}