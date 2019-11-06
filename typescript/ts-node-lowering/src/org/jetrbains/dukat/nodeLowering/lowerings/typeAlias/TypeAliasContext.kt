package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


private fun ParameterValueDeclaration?.resolveAsValue(): NameEntity? {
    return when (this) {
        is TypeValueNode -> this.value
        is TypeParameterNode -> this.name
        else -> null
    }
}

class TypeAliasContext {

    private fun TypeAliasNode.canSubstitute(heritageNode: HeritageNode): Boolean {
        return (name == heritageNode.name)
    }

    private fun ParameterValueDeclaration.specify(aliasParamsMap: Map<NameEntity, ParameterValueDeclaration>): ParameterValueDeclaration {

        return when (this) {
            is TypeParameterNode -> {
                val nameResolved = aliasParamsMap[name].resolveAsValue() ?: name
                copy(name = nameResolved, meta = meta?.specify(aliasParamsMap))
            }
            is TypeValueNode -> {
                val paramsSpecified = params.map { param ->
                    when (param) {
                        is TypeParameterNode -> {
                            substitute(aliasParamsMap.getOrDefault(param.name, param.specify(aliasParamsMap)))
                        }
                        is TypeValueNode -> {
                            substitute(aliasParamsMap.getOrDefault(param.value, param.specify(aliasParamsMap)))
                        }
                        else -> param
                    }
                }

                val valueResolved = aliasParamsMap[value].resolveAsValue() ?: value

                copy(value = valueResolved, params = paramsSpecified, meta = meta?.specify(aliasParamsMap))
            }
            is IntersectionMetadata -> {
                params.firstOrNull()?.meta = null
                // TODO: we can not make IntersectionMetadata data class since we'll end up in recursion - https://youtrack.jetbrains.com/issue/KT-29786
                IntersectionMetadata(params = params.map { param -> substitute(param).specify(aliasParamsMap) })
            }
            is UnionTypeNode -> {
                copy(params = params.map { param -> substitute(param).specify(aliasParamsMap) })
            }
            is FunctionTypeNode -> {
                copy(parameters = parameters.map { parameterDeclaration ->
                    parameterDeclaration.copy(type = substitute(parameterDeclaration.type).specify(aliasParamsMap))
                }, type = substitute(type).specify(aliasParamsMap))
            }
            else -> this
        }
    }

    private val myTypeAliasMap: MutableMap<String, TypeAliasNode> = mutableMapOf()

    fun registerTypeAlias(typeAlias: TypeAliasNode) {
        myTypeAliasMap[typeAlias.uid] = typeAlias
    }

    fun resolveTypeAlias(heritageClause: HeritageNode): ParameterValueDeclaration? {
        return myTypeAliasMap.values.firstOrNull { typeAlias ->
            typeAlias.canSubstitute(heritageClause)
        }?.typeReference
    }

    fun substitute(type: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (type) {
            is TypeValueNode ->
                myTypeAliasMap[type.typeReference?.uid]?.let { aliasResolved ->
                    when (aliasResolved.typeReference) {
                        is GeneratedInterfaceReferenceNode -> aliasResolved.typeReference
                        is TypeNode -> {
                            val aliasParamsMap: Map<NameEntity, ParameterValueDeclaration> = aliasResolved.typeParameters.zip(type.params).associateBy({ it.first }, { it.second })
                            aliasResolved.typeReference.specify(aliasParamsMap)
                        }
                        else -> null
                    }
                } ?: type
            is UnionTypeNode -> type.copy(params = type.params.map { param ->
                this.substitute(param)
            })
            else -> type
        }
    }
}