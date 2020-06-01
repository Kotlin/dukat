package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering

private class TypeSpecifierLowering(private val aliasParamMap: Map<NameEntity, TypeNode>) : NodeTypeLowering {

    private fun spec(declaration: TypeNode): TypeNode {
        val declarationResolved = when (declaration) {
            is TypeParameterNode -> aliasParamMap.getOrDefault(declaration.name, declaration)
            is TypeValueNode -> aliasParamMap.getOrDefault(declaration.value, declaration)
            else -> declaration
        }

        if (declarationResolved != declaration) {
            declarationResolved.meta = declaration.meta
        }

        return declarationResolved
    }

    override fun lowerMeta(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is IntersectionMetadata -> {
                IntersectionMetadata(params = declaration.params.map {
                    //TODO: this is our way of avoiding SOE on assigning meta
                    (when (it) {
                        is TypeNode -> lowerType(it)
                        else -> lowerMeta(it)
                    }).duplicate<ParameterValueDeclaration>()
                })
            }
            else -> super.lowerMeta(declaration)
        }
    }

    override fun lowerType(declaration: TypeNode): TypeNode {
        return spec(super.lowerType(declaration))
    }
}

class TypeAliasContext {

    private val myTypeAliasMap: MutableMap<String, TypeAliasNode> = mutableMapOf()

    fun registerTypeAlias(typeAlias: TypeAliasNode) {
        // TODO: this is our way of avoiding SOE on assigning meta
        myTypeAliasMap[typeAlias.uid] = typeAlias
    }
    
    fun dereference(declaration: TypeNode): TypeNode {
        return when (declaration) {
            is TypeValueNode ->
                myTypeAliasMap[declaration.typeReference?.uid]?.let { aliasResolved ->
                    val aliasParamsMap: Map<NameEntity, TypeNode> = aliasResolved.typeParameters.zip(declaration.params).associateBy({ it.first.value }, { it.second })
                    TypeSpecifierLowering(aliasParamsMap).lowerType(aliasResolved.typeReference)
                } ?: declaration
            else -> declaration
        }
    }

}