package org.jetbrains.dukat.compiler.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.HeritageSymbolDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.PropertyAccessDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

// TODO: TypeAliases should be revisited
private fun IdentifierDeclaration.translate() = value

private fun HeritageSymbolDeclaration.translate(): String {
    return when (this) {
        is IdentifierDeclaration -> translate()
        is PropertyAccessDeclaration -> expression.translate() + "." + name.translate()
        else -> throw Exception("unknown heritage clause ${this}")
    }
}

class TypeAliasContext {

    private fun TypeAliasDeclaration.canSusbtitute(heritageClause: HeritageClauseDeclaration): Boolean {
        return aliasName == heritageClause.name.translate()
    }

    private fun ParameterValueDeclaration.specify(aliasParamsMap: Map<String, ParameterValueDeclaration>): ParameterValueDeclaration {
        return when (this) {
            is TypeDeclaration -> {
                val paramsSpecified = params.map { param ->
                     when(param) {
                        is TypeDeclaration -> {
                            resolveTypeAlias(aliasParamsMap.getOrDefault(param.value, param.specify(aliasParamsMap)))
                        }
                        else -> param
                    }
                }

                val valueAliasResolved = aliasParamsMap.get(value)

                val valueResolved = if (valueAliasResolved is TypeDeclaration) {
                    valueAliasResolved.value
                } else value

                // TODO: there's somewhere a

                copy(value = valueResolved, params = paramsSpecified, meta = meta?.specify(aliasParamsMap))
            }
            is IntersectionMetadata -> {
                val paramsResolved = if (params.isEmpty()) {
                    params
                } else {
                    // IntersectionMetadata stores reference to param we've already specified
                    val allParams = params.toMutableList()
                    val firstParam = allParams.first()
                    firstParam.meta = null
                    allParams
                }
                // TODO: we can not make IntersectionMetadata data class since we'll end up in recursion - https://youtrack.jetbrains.com/issue/KT-29786
                IntersectionMetadata(params = paramsResolved.map {param -> resolveTypeAlias(param).specify(aliasParamsMap)} )
            }
            is UnionTypeNode -> {
                copy(params = params.map {param -> resolveTypeAlias(param).specify(aliasParamsMap)})
            }
            is UnionTypeDeclaration -> {
                copy(params = params.map {param -> resolveTypeAlias(param).specify(aliasParamsMap)})
            }
            is FunctionTypeDeclaration -> {
                copy(parameters = parameters.map {parameterDeclaration ->
                    parameterDeclaration.copy(type = resolveTypeAlias(parameterDeclaration.type).specify(aliasParamsMap))
                }, type = resolveTypeAlias(type).specify(aliasParamsMap))
            }
            else -> this
        }
    }

    private fun TypeAliasDeclaration.substitute(type: ParameterValueDeclaration): ParameterValueDeclaration? {
        return when (type) {
            is TypeDeclaration -> {
                if (aliasName == type.value) {
                    if (typeParameters.size == type.params.size) {
                        val aliasParamsMap = typeParameters.zip(type.params).associateBy({ it.first.value }, { it.second })

                        if (typeReference is TypeDeclaration) {
                            return typeReference.specify(aliasParamsMap)
                        } else if (typeReference is UnionTypeNode) {
                            return typeReference.specify(aliasParamsMap)
                        } else if (typeReference is FunctionTypeDeclaration) {
                            return typeReference.specify(aliasParamsMap)
                        }
                    }
                }

                null
            }
            is UnionTypeNode -> {
                type.copy(params = type.params.map { param ->
                    resolveTypeAlias(param)
                })
            }
            else -> null
        }
    }

    private val myTypeAliasDeclaration: MutableSet<TypeAliasDeclaration> = mutableSetOf()

    fun registerTypeAlias(typeAlias: TypeAliasDeclaration) {
        myTypeAliasDeclaration.add(typeAlias)
    }

    fun resolveTypeAlias(heritageClause: HeritageClauseDeclaration): ParameterValueDeclaration? {
        myTypeAliasDeclaration.forEach { typeAlias ->
            if (typeAlias.canSusbtitute(heritageClause)) {
                return typeAlias.typeReference
            }
        }

        return null
    }

    fun resolveTypeAlias(type: ParameterValueDeclaration): ParameterValueDeclaration {


        myTypeAliasDeclaration.forEach { typeAlias ->
            typeAlias.substitute(type)?.let {
                return it
            }
        }

        return type
    }
}