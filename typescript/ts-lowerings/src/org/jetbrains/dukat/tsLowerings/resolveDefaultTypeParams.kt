package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration

private class ResolveDefaultTypeParams(private val references: Map<String, ClassLikeDeclaration>) : DeclarationTypeLowering {

    private fun resolveParameters(typeReference: ReferenceEntity?, parameters: List<ParameterValueDeclaration>): List<ParameterValueDeclaration> {
        val paramsResolved = parameters.toMutableList()

        references[typeReference?.uid]?.let { reference ->

            if (parameters.size < reference.typeParameters.size) {
                val typeReferenceMap = reference.typeParameters.foldRightIndexed(mutableMapOf<NameEntity, Int>()) { index, typeParam, acc ->
                    acc[typeParam.name] = index
                    acc
                }

                var fromIndex = reference.typeParameters.size - parameters.size
                if (parameters.isEmpty()) {
                    fromIndex -= 1
                }

                for (i in fromIndex until reference.typeParameters.size) {
                    reference.typeParameters[i].defaultValue?.let { defValue ->
                        val value = when (defValue) {
                            is ObjectLiteralDeclaration -> if (defValue.members.isEmpty()) {
                                TypeDeclaration(IdentifierEntity("Any"), emptyList())
                            } else {
                                defValue
                            }
                            is TypeDeclaration -> defValue.value
                            is TypeParamReferenceDeclaration -> defValue.value
                            else -> null
                        }

                        if (value != null) {
                            val defIndex = typeReferenceMap[value]

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

    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>): HeritageClauseDeclaration {
        val params = resolveParameters(heritageClause.typeReference, heritageClause.typeArguments)
        return super.lowerHeritageClause(heritageClause.copy(typeArguments = params), owner)
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): TypeDeclaration {
        return super.lowerTypeDeclaration(declaration.copy(params = resolveParameters(declaration.typeReference, declaration.params)), owner)
    }
}

private fun ModuleDeclaration.resolveDefaultTypeParams(): ModuleDeclaration {
    return ResolveDefaultTypeParams(collectReferences()).lowerDocumentRoot(this)
}

private fun SourceFileDeclaration.resolveDefaultTypeParams() = copy(root = root.resolveDefaultTypeParams())

fun SourceSetDeclaration.resolveDefaultTypeParams() = copy(sources = sources.map(SourceFileDeclaration::resolveDefaultTypeParams))
