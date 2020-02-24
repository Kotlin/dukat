package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


internal data class DereferenceNode(
        val dereferenced: TypeNode,
        val aliasParamsMap: Map<NameEntity, ParameterValueDeclaration>,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration

class TypeAliasContext {

    private val myTypeAliasMap: MutableMap<String, TypeAliasNode> = mutableMapOf()

    fun registerTypeAlias(typeAlias: TypeAliasNode) {
        // TODO: this is our way of avoiding SOE on assigning meta
        myTypeAliasMap[typeAlias.uid] = typeAlias
    }
    
    fun dereference(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeValueNode ->
                myTypeAliasMap[declaration.typeReference?.uid]?.let { aliasResolved ->
                    when (val typeReference = aliasResolved.typeReference) {
                        is TypeNode -> {
                            val aliasParamsMap: Map<NameEntity, ParameterValueDeclaration> = aliasResolved.typeParameters.zip(declaration.params).associateBy({ it.first }, { it.second })
                            DereferenceNode(typeReference, aliasParamsMap)
                        }
                        else -> typeReference
                    }
                } ?: declaration
            else -> declaration
        }
    }

}