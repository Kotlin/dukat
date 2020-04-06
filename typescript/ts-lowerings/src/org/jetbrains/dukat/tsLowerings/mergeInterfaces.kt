package org.jetbrains.dukat.tsLowerings

import MergeableDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

private fun MethodSignatureDeclaration.convertToMethod(): FunctionDeclaration {
    return FunctionDeclaration(
            name = name,
            parameters = parameters,
            typeParameters = typeParameters,
            type = type,
            modifiers = modifiers,
            body = null,
            uid = "__NO_UID__"
    )
}

private fun mergeParentEntities(parentEntitiesA: List<HeritageClauseDeclaration>, parentEntitiesB: List<HeritageClauseDeclaration>): List<HeritageClauseDeclaration> {
    val parentSet = parentEntitiesA.toSet()
    return parentEntitiesA + parentEntitiesB.filter { parentEntity -> !parentSet.contains(parentEntity) }
}

private fun mergeVariableAndInterface(a: VariableDeclaration, b: InterfaceDeclaration): InterfaceDeclaration {
    println("MERGE VAR ${a} and ${b}")
    return b
}

private fun mergeClassAndInterface(a: ClassDeclaration, b: InterfaceDeclaration): ClassDeclaration {
    val membersResolved = b.members.map {
        when (it) {
            is MethodSignatureDeclaration -> it.convertToMethod()
            else -> it
        }
    } + a.members
    return a.copy(
            members = membersResolved,
            typeParameters = if (b.typeParameters.size > a.typeParameters.size) {b.typeParameters} else {a.typeParameters},
            parentEntities = mergeParentEntities(a.parentEntities, b.parentEntities)
    )
}

private fun mergeInterfaces(a: InterfaceDeclaration, b: InterfaceDeclaration): InterfaceDeclaration {
    return a.copy(
            members = b.members + a.members,
            typeParameters = if (b.typeParameters.size > a.typeParameters.size) {b.typeParameters} else {a.typeParameters},
            parentEntities = mergeParentEntities(a.parentEntities, b.parentEntities)
    )
}

private fun merge(a: TopLevelDeclaration, b: TopLevelDeclaration): TopLevelDeclaration {
    return when (a) {
        is InterfaceDeclaration -> when (b) {
            is InterfaceDeclaration -> mergeInterfaces(a, b)
            is ClassDeclaration -> mergeClassAndInterface(b, a)
            else -> a
        }
        is ClassDeclaration -> when (b) {
            is InterfaceDeclaration -> mergeClassAndInterface(a, b)
            else -> a
        }
        is VariableDeclaration -> when (b) {
            is InterfaceDeclaration -> mergeVariableAndInterface(a, b)
            else -> a
        }
        else -> a
    }
}

private fun ModuleDeclaration.mergeInterfaces(topLevelDeclarationResolver: TopLevelDeclarationResolver): ModuleDeclaration {
    val declarationsResolved = declarations.mapNotNull {
        if (it is MergeableDeclaration) {
            val definitions = it.definitionsInfo.mapNotNull { definition -> topLevelDeclarationResolver.resolve(definition.uid) }
            val onlyInterfaces = definitions.all { it is InterfaceDeclaration }
            if (onlyInterfaces) {
                if (it.uid == it.definitionsInfo.firstOrNull()?.uid) {
                    definitions.reduce { acc, definitionInfoDeclaration -> merge(acc, definitionInfoDeclaration) }
                } else {
                    null
                }
            } else {
                it
            }
        } else if (it is ModuleDeclaration) {
            it.mergeInterfaces(topLevelDeclarationResolver)
        } else {
            it
        }
    }
    return copy(declarations = declarationsResolved)
}

private fun SourceSetDeclaration.mergeInterfaces(topLevelDeclarationResolver: TopLevelDeclarationResolver): SourceSetDeclaration {
    return copy(sources = sources.map { it.copy(root = it.root.mergeInterfaces(topLevelDeclarationResolver)) })
}

class MergeInterfaces() : TsLowering {

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val topLevelDeclarationResolver = TopLevelDeclarationResolver(source)
        return source.mergeInterfaces(topLevelDeclarationResolver)
    }
}