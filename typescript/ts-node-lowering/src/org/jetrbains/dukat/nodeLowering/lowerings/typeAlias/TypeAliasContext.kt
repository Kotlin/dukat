package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


internal data class DereferenceNode(
        val dereferenced: ParameterValueDeclaration,
        val aliasParamsMap: Map<NameEntity, ParameterValueDeclaration>,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration

class TypeAliasContext {

    private fun TypeAliasNode.canSubstitute(heritageNode: HeritageNode): Boolean {
        return (name == heritageNode.name)
    }

    private val myTypeAliasMap: MutableMap<String, TypeAliasNode> = mutableMapOf()

    fun registerTypeAlias(typeAlias: TypeAliasNode) {
        // TODO: this is our way of avoiding SOE on assigning meta
        myTypeAliasMap[typeAlias.uid] = typeAlias
    }

    fun resolveTypeAlias(heritageClause: HeritageNode): ParameterValueDeclaration? {
        return myTypeAliasMap.values.firstOrNull { typeAlias ->
            typeAlias.canSubstitute(heritageClause)
        }?.typeReference
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
                        is IntersectionMetadata -> {
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