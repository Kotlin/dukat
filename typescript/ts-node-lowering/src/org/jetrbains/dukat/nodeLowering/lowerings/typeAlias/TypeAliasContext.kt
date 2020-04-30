package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


internal data class DereferenceNode(
        val dereferenced: TypeNode,
        val aliasParamsMap: Map<NameEntity, TypeNode>,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : TypeNode

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
                    when (val typeReference = aliasResolved.typeReference) {
                        else -> {
                            val aliasParamsMap: Map<NameEntity, TypeNode> = aliasResolved.typeParameters.zip(declaration.params).associateBy({ it.first.value }, { it.second })
                            DereferenceNode(typeReference, aliasParamsMap)
                        }
                    }
                } ?: declaration
            else -> declaration
        }
    }

}