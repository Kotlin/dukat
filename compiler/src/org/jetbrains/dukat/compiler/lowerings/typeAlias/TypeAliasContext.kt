package org.jetbrains.dukat.compiler.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

class TypeAliasContext {

    private fun TypeAliasNode.canSusbtitute(heritageNode: HeritageNode): Boolean {
        return (!canBeTranslated) && (name == heritageNode.name)
    }

    private fun ParameterValueDeclaration.specify(aliasParamsMap: Map<NameEntity, ParameterValueDeclaration>): ParameterValueDeclaration {
        return when (this) {
            is TypeValueNode -> {
                val paramsSpecified = params.map { param ->
                    when (param) {
                        is TypeValueNode -> {
                            resolveTypeAlias(aliasParamsMap.getOrDefault(param.value, param.specify(aliasParamsMap)))
                        }
                        else -> param
                    }
                }

                val valueAliasResolved = aliasParamsMap.get(value)

                val valueResolved = if (valueAliasResolved is TypeValueNode) {
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

    private fun TypeAliasNode.substitute(type: ParameterValueDeclaration): ParameterValueDeclaration? {
        return when (type) {
            is TypeValueNode -> {
                if (type.value == name) {
                    if (typeParameters.size == type.params.size) {
                        val aliasParamsMap: Map<NameEntity, ParameterValueDeclaration> = typeParameters.zip(type.params).associateBy({ it.first }, { it.second })

                        when (typeReference) {
                            is TypeValueNode -> return typeReference.specify(aliasParamsMap)
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

    private val myTypeAliasNodes: MutableSet<TypeAliasNode> = mutableSetOf()

    fun registerTypeAlias(typeAlias: TypeAliasNode) {
        myTypeAliasNodes.add(typeAlias)
    }

    fun resolveTypeAlias(heritageClause: HeritageNode): ParameterValueDeclaration? {
        myTypeAliasNodes.forEach { typeAlias ->
            if (typeAlias.canSusbtitute(heritageClause)) {
                return typeAlias.typeReference
            }
        }

        return null
    }

    fun resolveTypeAlias(type: ParameterValueDeclaration): ParameterValueDeclaration {

        myTypeAliasNodes.forEach { typeAlias ->
            typeAlias.substitute(type)?.let {
                return it
            }
        }


        return type
    }
}