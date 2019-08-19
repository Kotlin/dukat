package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering

private class LowerTypeAliases(val context: TypeAliasContext) : NodeTypeLowering {
    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {

        val parentEntitiesRemapped = declaration.parentEntities.map { parent ->
            val resolved = context.resolveTypeAlias(parent)

            if (resolved is TypeValueNode) {
                val typeNodeValue = resolved.value
                when (typeNodeValue) {
                    is IdentifierEntity -> HeritageNode(IdentifierEntity(typeNodeValue.value), emptyList())
                    else -> raiseConcern("unknown NameEntity $typeNodeValue") { parent }
                }
            } else {
                parent
            }
        }

        return super.lowerInterfaceNode(declaration.copy(parentEntities = parentEntitiesRemapped))
    }

    private fun ParameterValueDeclaration.unroll(): List<ParameterValueDeclaration> {
        return when (this) {
            is UnionTypeNode -> {
                val paramsUnrolled = params.flatMap { param -> param.unroll() }
                paramsUnrolled.toSet().toList()
            }
            else -> listOf(this)
        }
    }


    override fun lowerUnionTypeNode(declaration: UnionTypeNode): UnionTypeNode {
        return super.lowerUnionTypeNode(declaration.copy(params = declaration.unroll()))
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return super.lowerType(context.resolveTypeAlias(declaration))
    }

}

private fun TypeAliasNode.shouldBeTranslated(): Boolean {
    return when (this.typeReference) {
        is TypeValueNode -> this.typeReference.meta == null
        is FunctionTypeNode -> true
        else -> false
    }
}

private fun DocumentRootNode.registerTypeAliases(astContext: TypeAliasContext) {
    declarations.forEach { declaration ->
        if (declaration is TypeAliasNode) {

            declaration.canBeTranslated = declaration.shouldBeTranslated()

            if (!declaration.canBeTranslated) {
                astContext.registerTypeAlias(
                        declaration.copy(typeReference = astContext.resolveTypeAlias(declaration.typeReference))
                )
            }
        } else if (declaration is DocumentRootNode) {
            declaration.registerTypeAliases(astContext)
        }
    }
}

fun DocumentRootNode.resolveTypeAliases(astContext: TypeAliasContext): DocumentRootNode {
    return LowerTypeAliases(astContext).lowerDocumentRoot(this)
}

fun SourceSetNode.resolveTypeAliases(): SourceSetNode  {
    val astContext = TypeAliasContext()

    sources.forEach { source ->
        source.root.registerTypeAliases(astContext)
    }

    return copy(sources = sources.map { source ->
        source.copy(root = source.root.resolveTypeAliases(astContext))
    })
}