package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class ResolveDefaultTypeParams(private val references: Map<String, ClassLikeDeclaration>) : DeclarationTypeLowering {

    private fun resolveParameters(typeReference: ReferenceEntity<ClassLikeDeclaration>?, parameters: List<ParameterValueDeclaration>): List<ParameterValueDeclaration> {
        val paramsResolved = parameters.toMutableList()

        references[typeReference?.uid]?.let { reference ->

            if (parameters.size < reference.typeParameters.size) {
                val typeReferenceMap = reference.typeParameters.foldRightIndexed(mutableMapOf<NameEntity, Int>()) { index, typeParam, acc ->
                    acc[typeParam.name] = index
                    acc
                }


                val fromIndex = reference.typeParameters.size - parameters.size

                for (i in fromIndex until reference.typeParameters.size) {
                    reference.typeParameters[i].defaultValue?.let { defValue ->
                        if (defValue is TypeDeclaration) {
                            val defIndex = typeReferenceMap[defValue.value]

                            val parameterValue = if (defIndex == null) {
                                defValue
                            } else {
                                parameters[defIndex]
                            }

                            paramsResolved.add(parameterValue)
                        }
                    }

                }
            }
        }

        return paramsResolved
    }

    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration): HeritageClauseDeclaration {
        val params = resolveParameters(heritageClause.typeReference, heritageClause.typeArguments)
        return super.lowerHeritageClause(heritageClause.copy(typeArguments = params))
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return super.lowerTypeDeclaration(declaration.copy(params = resolveParameters(declaration.typeReference as ReferenceEntity<ClassLikeDeclaration>?, declaration.params)))
    }
}

fun ModuleDeclaration.collectReferences(references: MutableMap<String, ClassLikeDeclaration>): Map<String, ClassLikeDeclaration> {
    declarations.forEach {
        if (it is ClassDeclaration) {
            references[it.uid] = it
        } else if (it is InterfaceDeclaration) {
            references[it.uid] = it
        } else if (it is ModuleDeclaration) {
            it.collectReferences(references)
        }
    }

    return references
}

private fun ModuleDeclaration.resolveDefaultTypeParams(): ModuleDeclaration {
    val references: Map<String, ClassLikeDeclaration> =
            collectReferences(mutableMapOf())

    return ResolveDefaultTypeParams(references).lowerDocumentRoot(this)
}

private fun SourceFileDeclaration.resolveDefaultTypeParams() = copy(root = root.resolveDefaultTypeParams())

fun SourceSetDeclaration.resolveDefaultTypeParams() = copy(sources = sources.map(SourceFileDeclaration::resolveDefaultTypeParams))
