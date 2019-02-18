package org.jetbrains.dukat.compiler.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeNodeValue
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

// TODO: TypeAliases should be revisited
private fun IdentifierNode.translate() = value

private fun HeritageSymbolNode.translate(): String {
    return when (this) {
        is IdentifierNode -> translate()
        is PropertyAccessNode -> expression.translate() + "." + name.translate()
        else -> throw Exception("unknown heritage clause ${this}")
    }
}

private fun TypeNodeValue.getAliasKey(): String {
    return when(this) {
        is IdentifierNode -> translate()
        else -> throw Exception("unknown TypeNodeValue ${this}")
    }
}


class TypeAliasContext {

    private fun TypeAliasDeclaration.canSusbtitute(heritageNode: HeritageNode): Boolean {
        return aliasName == heritageNode.name.translate()
    }

    private fun ParameterValueDeclaration.specify(aliasParamsMap: Map<String, ParameterValueDeclaration>): ParameterValueDeclaration {
        return when (this) {
            is TypeNode -> {
                val paramsSpecified = params.map { param ->
                    when (param) {
                        is TypeNode -> {
                            resolveTypeAlias(aliasParamsMap.getOrDefault(param.value.getAliasKey(), param.specify(aliasParamsMap)))
                        }
                        else -> param
                    }
                }

                val valueAliasResolved = aliasParamsMap.get(value.getAliasKey())

                val valueResolved = if (valueAliasResolved is TypeNode) {
                    valueAliasResolved.value
                } else value

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
                IntersectionMetadata(params = paramsResolved.map { param -> resolveTypeAlias(param).specify(aliasParamsMap) })
            }
            is UnionTypeNode -> {
                copy(params = params.map { param -> resolveTypeAlias(param).specify(aliasParamsMap) })
            }
            is FunctionTypeNode -> {
                copy(parameters = parameters.map { parameterDeclaration ->
                    parameterDeclaration.copy(type = resolveTypeAlias(parameterDeclaration.type).specify(aliasParamsMap))
                }, type = resolveTypeAlias(type).specify(aliasParamsMap))
            }
            else -> this
        }
    }

    private fun TypeAliasDeclaration.substitute(type: ParameterValueDeclaration): ParameterValueDeclaration? {
        return when (type) {
            is TypeNode -> {
                if (type.isPrimitive(aliasName)) {
                    if (typeParameters.size == type.params.size) {
                        val aliasParamsMap = typeParameters.zip(type.params).associateBy({ it.first.value }, { it.second })

                        when (typeReference) {
                            is TypeNode -> return typeReference.specify(aliasParamsMap)
                            is UnionTypeNode -> return typeReference.specify(aliasParamsMap)
                            is FunctionTypeNode -> return typeReference.specify(aliasParamsMap)
                            is GeneratedInterfaceReferenceDeclaration -> return typeReference
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

    fun resolveTypeAlias(heritageClause: HeritageNode): ParameterValueDeclaration? {
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