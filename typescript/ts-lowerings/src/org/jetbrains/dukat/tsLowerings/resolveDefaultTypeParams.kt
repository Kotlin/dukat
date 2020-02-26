package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration

private class ResolveDefaultTypeParams(private val references: Map<String, ClassLikeDeclaration>) : DeclarationTypeLowering {

    private fun resolveParameters(typeReference: ReferenceEntity?, parameters: List<ParameterValueDeclaration>): List<ParameterValueDeclaration> {
        val defaultParams = mutableListOf<ParameterValueDeclaration>()

        references[typeReference?.uid]?.let { reference ->

            if (parameters.size < reference.typeParameters.size) {

                val typeReferenceMap: Map<NameEntity, Int> by lazy {
                    reference.typeParameters.mapIndexed { index, typeParam ->
                        Pair(typeParam.name, index)
                    }.toMap()
                }

                val missingTypeParams = reference.typeParameters.subList(parameters.size, reference.typeParameters.size)

                missingTypeParams.forEach {
                    it.defaultValue?.let { defTypeParamValue ->
                        when (defTypeParamValue) {
                            is TypeParamReferenceDeclaration -> {
                                typeReferenceMap[defTypeParamValue.value]?.let { resolvedIndex ->
                                    parameters[resolvedIndex].let { selfReferenceValue ->
                                        defaultParams.add(selfReferenceValue)
                                    }
                                }
                            }
                            else -> defaultParams.add(defTypeParamValue)
                        }
                    }
                }
            }

        }

        return parameters + defaultParams
    }

    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>?): HeritageClauseDeclaration {
        val params = resolveParameters(heritageClause.typeReference, heritageClause.typeArguments)
        return super.lowerHeritageClause(heritageClause.copy(typeArguments = params), owner)
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        return super.lowerTypeDeclaration(declaration.copy(params = resolveParameters(declaration.typeReference, declaration.params)), owner)
    }
}

private fun ModuleDeclaration.resolveDefaultTypeParams(references: Map<String, ClassLikeDeclaration>): ModuleDeclaration {
    return ResolveDefaultTypeParams(references).lowerDocumentRoot(this)
}


fun SourceSetDeclaration.resolveDefaultTypeParams(): SourceSetDeclaration {
    val references = mutableMapOf<String, ClassLikeDeclaration>()

    sources.forEach { source -> source.root.collectReferences(references) }

    return copy(sources = sources.map { it.copy(root = it.root.resolveDefaultTypeParams(references)) })
}
