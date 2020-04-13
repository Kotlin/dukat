package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration

private fun NameEntity.addPostfix(postfix: String): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierEntity(value + postfix)
        is QualifierEntity -> copy(left = left.addPostfix(postfix))
    }
}

private class SubstituteTypeLowering(private val generatedEntities: Map<String, Map<Int, ClassLikeDeclaration>>) : DeclarationTypeLowering {

    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>?): HeritageClauseDeclaration {
        val heritageClauseResolved = generatedEntities[heritageClause.typeReference?.uid]?.get(heritageClause.typeArguments.size)?.let { reference ->
            heritageClause.copy(name = reference.name, typeReference = ReferenceDeclaration(reference.uid))
        }

        return super.lowerHeritageClause(heritageClauseResolved ?: heritageClause, owner)
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        val declarationResolved = generatedEntities[declaration.typeReference?.uid]?.get(declaration.params.size)?.let { reference ->
            declaration.copy(value = reference.name, typeReference = ReferenceDeclaration(reference.uid))
        }

        return super.lowerTypeDeclaration(declarationResolved ?: declaration, owner)
    }
}

private class EntityWithDefaultTypeParamsGenerator(private val references: Map<String, ClassLikeDeclaration>, private val generatedEntities: MutableMap<String, MutableMap<Int, ClassLikeDeclaration>>) : DeclarationTypeLowering {

    private fun checkForDefaultTypeParams(declarationParams: List<ParameterValueDeclaration>, resolvedClassLike: ClassLikeDeclaration?) {
        if (resolvedClassLike != null) {
            val typeParameters = resolvedClassLike.typeParameters
            if (declarationParams.size < typeParameters.size) {
                val postfix = "__${declarationParams.size}"
                val uid = resolvedClassLike.uid + postfix


                generatedEntities
                        .getOrPut(resolvedClassLike.uid) { mutableMapOf() }
                        .getOrPut(declarationParams.size) {

                            val defValues = typeParameters.subList(declarationParams.size, typeParameters.size).mapNotNull {
                                val defValue = it.defaultValue
                                when (defValue) {
                                    is ObjectLiteralDeclaration -> {
                                         if (defValue.members.isEmpty()) {
                                            TypeDeclaration(IdentifierEntity("Any"), emptyList())
                                             defValue
                                        } else {
                                            defValue
                                        }
                                    }
                                    else -> defValue
                                }
                            }
                            val headValues = typeParameters.subList(0, declarationParams.size)

                            when (resolvedClassLike) {
                                is InterfaceDeclaration -> InterfaceDeclaration(
                                        name = resolvedClassLike.name.addPostfix(postfix),
                                        members = emptyList(),
                                        uid = uid,
                                        typeParameters = headValues,
                                        parentEntities = listOf(HeritageClauseDeclaration(
                                                name = resolvedClassLike.name,
                                                typeArguments = headValues.map { TypeParamReferenceDeclaration(it.name) } + defValues,
                                                extending = false,
                                                typeReference = ReferenceDeclaration(resolvedClassLike.uid)
                                        )),
                                        definitionsInfo = emptyList()
                                )
                                is ClassDeclaration -> ClassDeclaration(
                                        name = resolvedClassLike.name.addPostfix(postfix),
                                        members = emptyList(),
                                        uid = uid,
                                        typeParameters = headValues,
                                        parentEntities = listOf(HeritageClauseDeclaration(
                                                name = resolvedClassLike.name,
                                                typeArguments = headValues.map { TypeParamReferenceDeclaration(it.name) } + defValues,
                                                extending = false,
                                                typeReference = ReferenceDeclaration(resolvedClassLike.uid)
                                        )),
                                        modifiers = resolvedClassLike.modifiers,
                                        definitionsInfo = resolvedClassLike.definitionsInfo
                                )

                                else -> resolvedClassLike
                            }
                        }
            }
        }

    }

    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>?): HeritageClauseDeclaration {
        checkForDefaultTypeParams(heritageClause.typeArguments, references[heritageClause.typeReference?.uid])
        return super.lowerHeritageClause(heritageClause, owner)
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        checkForDefaultTypeParams(declaration.params, references[declaration.typeReference?.uid])
        return super.lowerTypeDeclaration(declaration, owner)
    }

}

private fun ModuleDeclaration.introduceGeneratedEntities(generatedEntities: Map<String, Map<Int, ClassLikeDeclaration>>): ModuleDeclaration {
    val declarationsResolved = declarations.flatMap { declaration ->
        when (declaration) {
            is ModuleDeclaration -> listOf(declaration.introduceGeneratedEntities(generatedEntities))
            is ClassLikeDeclaration -> {
                (generatedEntities[declaration.uid]?.values)?.let {
                    listOf(declaration) + it
                } ?: listOf(declaration)
            }
            else -> listOf(declaration)
        }
    }

    return copy(declarations = declarationsResolved)
}

private fun SourceSetDeclaration.introduceSubstitutedTypes(references: MutableMap<String, ClassLikeDeclaration>, generatedEntities: MutableMap<String, MutableMap<Int, ClassLikeDeclaration>>): SourceSetDeclaration {
    return copy(sources = sources.map { it.copy(root = EntityWithDefaultTypeParamsGenerator(references, generatedEntities).lowerSourceDeclaration(it.root)) })
}


private fun SourceSetDeclaration.resolveDefaultTypeParams(): SourceSetDeclaration {
    val references = mutableMapOf<String, ClassLikeDeclaration>()
    val generatedEntities = mutableMapOf<String, MutableMap<Int, ClassLikeDeclaration>>()

    sources.forEach { source -> source.root.collectReferences(references) }

    val sourceSetWithSubstitutedTypes = introduceSubstitutedTypes(references, generatedEntities)
    val sourceSetsWithIntroducedEntities = sourceSetWithSubstitutedTypes.copy(sources = sourceSetWithSubstitutedTypes.sources.map { it.copy(root = it.root.introduceGeneratedEntities(generatedEntities)) })
    return sourceSetsWithIntroducedEntities.copy(sources = sourceSetsWithIntroducedEntities.sources.map { it.copy(root = SubstituteTypeLowering(generatedEntities).lowerSourceDeclaration(it.root)) })
}

public class ResolveDefaultTypeParams(): TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.resolveDefaultTypeParams()
    }
}