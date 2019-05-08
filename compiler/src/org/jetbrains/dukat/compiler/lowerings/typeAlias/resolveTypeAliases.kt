package org.jetbrains.dukat.compiler.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetrbains.dukat.nodeLowering.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class LowerTypeAliases(val context: TypeAliasContext) : ParameterValueLowering {
    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {

        val parentEntitiesRemapped = declaration.parentEntities.map { parent ->
            val resolved = context.resolveTypeAlias(parent)

            if (resolved is TypeValueNode) {
                val typeNodeValue = resolved.value
                when (typeNodeValue) {
                    is IdentifierNode -> HeritageNode(IdentifierNode(typeNodeValue.value), emptyList())
                    else -> throw Exception("unknown ValueTypeNodeValue $typeNodeValue")
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

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return super.lowerParameterValue(context.resolveTypeAlias(declaration))
    }

}

private fun TypeAliasNode.shouldBeTranslated(): Boolean {
    return when(this.typeReference) {
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

fun DocumentRootNode.resolveTypeAliases(): DocumentRootNode {
    val astContext = TypeAliasContext()
    registerTypeAliases(astContext)
    return LowerTypeAliases(astContext).lowerDocumentRoot(this)
}

fun SourceSetNode.resolveTypeAliases() = transform { it.resolveTypeAliases() }