package org.jetbrains.dukat.tsLowerings

import MergeableDeclaration
import TopLevelDeclarationLowering
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration

private fun MethodSignatureDeclaration.convertToMethod(): FunctionDeclaration {
    return FunctionDeclaration(
            name = name,
            parameters = parameters,
            typeParameters = typeParameters,
            type = type,
            modifiers = modifiers,
            body = null,
            definitionsInfo = emptyList(),
            uid = "__NO_UID__",
            isGenerator = false
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


private fun mergeInterfaces(a: InterfaceDeclaration, b: InterfaceDeclaration): InterfaceDeclaration {
    return a.copy(
            members = b.members + a.members,
            typeParameters = if (b.typeParameters.size > a.typeParameters.size) {
                b.typeParameters
            } else {
                a.typeParameters
            },
            parentEntities = mergeParentEntities(a.parentEntities, b.parentEntities)
    )
}


private class SpecifyTypeReferenceLowering(private val typeParamMap: Map<NameEntity, ParameterValueDeclaration?>) : DeclarationLowering {
    override fun lowerTypeParamReferenceDeclaration(declaration: TypeParamReferenceDeclaration): ParameterValueDeclaration {
        return typeParamMap.get(declaration.value) ?: declaration
    }
}

private class MergeClassLikesLowering(private val topLevelDeclarationResolver: TopLevelDeclarationResolver) : TopLevelDeclarationLowering {

    private fun resolveParentMethods(classLikeDeclaration: ClassLikeDeclaration): List<MemberDeclaration> {
        return classLikeDeclaration.parentEntities.flatMap { heritageClause ->
            val parentClass = topLevelDeclarationResolver.resolveRecursive(heritageClause.typeReference?.uid)
            if (parentClass is ClassLikeDeclaration) {
                val typeParams = parentClass.typeParameters.mapIndexed { index, typeParam ->
                    Pair(typeParam.name, heritageClause.typeArguments.getOrNull(index))
                }.toMap()

                ((SpecifyTypeReferenceLowering(typeParams).lowerClassLikeDeclaration(parentClass, null) as? ClassLikeDeclaration)?.let { resolveClass ->
                    resolveClass.members
                } ?: emptyList()) + resolveParentMethods(parentClass)
            } else {
                emptyList()
            }
        }
    }

    private fun mergeClassAndInterface(a: ClassDeclaration, b: InterfaceDeclaration, uid: String): ClassDeclaration {
        val parentMembers = resolveParentMethods(b)
        val membersResolved = (parentMembers + b.members).map {
            when (it) {
                is MethodSignatureDeclaration -> it.convertToMethod()
                else -> it
            }
        } + a.members
        return a.copy(
                members = membersResolved,
                typeParameters = if (b.typeParameters.size > a.typeParameters.size) {
                    b.typeParameters
                } else {
                    a.typeParameters
                },
                parentEntities = mergeParentEntities(a.parentEntities, b.parentEntities),
                uid = uid
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

    override fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? {
        val definitions = declaration.definitionsInfo
                .mapNotNull { definition -> topLevelDeclarationResolver.resolve(definition.uid) }
                .filterIsInstance(ClassLikeDeclaration::class.java)

        val onlyClassLikes = definitions.isNotEmpty()
        return if (onlyClassLikes) {
            if (declaration.uid == definitions.firstOrNull()?.uid) {
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
    return copy(sources = sources.map { it.copy(root = MergeClassLikesLowering(topLevelDeclarationResolver).lowerSourceDeclaration(it.root)) })
}

class MergeClassLikes : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val topLevelDeclarationResolver = TopLevelDeclarationResolver(source)
        return source.mergeInterfaces(topLevelDeclarationResolver)
    }
}