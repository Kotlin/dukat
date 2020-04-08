package org.jetbrains.dukat.tsLowerings

import MergeableDeclaration
import TopLevelDeclarationLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
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

private fun mergeClassAndInterface(a: ClassDeclaration, b: InterfaceDeclaration, uid: String): ClassDeclaration {
    val membersResolved = b.members.map {
        when (it) {
            is MethodSignatureDeclaration -> it.convertToMethod()
            else -> it
        }
    } + a.members
    return a.copy(
            members = membersResolved,
            typeParameters = if (b.typeParameters.size > a.typeParameters.size) {b.typeParameters} else {a.typeParameters},
            parentEntities = mergeParentEntities(a.parentEntities, b.parentEntities),
            uid = uid
    )
}

private fun mergeInterfaces(a: InterfaceDeclaration, b: InterfaceDeclaration): InterfaceDeclaration {
    return a.copy(
            members = b.members + a.members,
            typeParameters = if (b.typeParameters.size > a.typeParameters.size) {b.typeParameters} else {a.typeParameters},
            parentEntities = mergeParentEntities(a.parentEntities, b.parentEntities)
    )
}

private fun merge(a: MergeableDeclaration, b: MergeableDeclaration): MergeableDeclaration {
    return when (a) {
        is InterfaceDeclaration -> when (b) {
            is InterfaceDeclaration -> mergeInterfaces(a, b)
            is ClassDeclaration -> mergeClassAndInterface(b, a, a.uid)
            else -> a
        }
        is ClassDeclaration -> when (b) {
            is InterfaceDeclaration -> mergeClassAndInterface(a, b, a.uid)
            else -> a
        }
        is VariableDeclaration -> when (b) {
            is InterfaceDeclaration -> mergeVariableAndInterface(a, b)
            else -> a
        }
        else -> a
    }
}

private class MergeClassLikesLowering(private val topLevelDeclarationResolver: TopLevelDeclarationResolver): TopLevelDeclarationLowering {
    override fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? {
        val definitions = declaration.definitionsInfo.mapNotNull { definition -> topLevelDeclarationResolver.resolve(definition.uid) }
        val onlyClassLikes = definitions.all { it is ClassLikeDeclaration }
        return if (onlyClassLikes) {
            if (declaration.uid == declaration.definitionsInfo.firstOrNull()?.uid) {
                @Suppress("UNCHECKED_CAST")
                (definitions as List<MergeableDeclaration>).reduce { acc, definitionInfoDeclaration -> merge(acc, definitionInfoDeclaration) }
            } else {
                null
            }
        } else {
            declaration
        }
    }
}

private fun SourceSetDeclaration.mergeInterfaces(topLevelDeclarationResolver: TopLevelDeclarationResolver): SourceSetDeclaration {
    return copy(sources = sources.map { it.copy(root = MergeClassLikesLowering(topLevelDeclarationResolver).lowerDocumentRoot(it.root)) })
}

class MergeClassLikes() : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val topLevelDeclarationResolver = TopLevelDeclarationResolver(source)
        return source.mergeInterfaces(topLevelDeclarationResolver)
    }
}