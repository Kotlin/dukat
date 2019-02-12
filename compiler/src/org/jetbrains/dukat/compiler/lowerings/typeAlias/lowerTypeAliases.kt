package org.jetbrains.dukat.compiler.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.compiler.lowerings.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.GenericParamDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.TokenDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class LowerTypeAliases(val context: TypeAliasContext) : ParameterValueLowering {
    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {

        val parentEntitiesRemapped = declaration.parentEntities.map { parent ->
            val resolved = context.resolveTypeAlias(parent)

            if (resolved is TypeDeclaration) {
                HeritageClauseDeclaration(IdentifierDeclaration(resolved.value), emptyList(), false)
            } else {
                parent
            }
        }

        return super.lowerInterfaceNode(declaration.copy(parentEntities = parentEntitiesRemapped))
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        val resolved = context.resolveTypeAlias(declaration)
        return resolved ?: declaration
    }

}

private fun TypeAliasDeclaration.introduceGenericParams(): TypeAliasDeclaration {
    val typeParamsSet = typeParameters.map { it.value }.toSet()

    return if (typeReference is TypeDeclaration) {
        val typeReferenceDeclaration = typeReference as TypeDeclaration
        val params = typeReferenceDeclaration.params.map { param ->
            if ((param is TypeDeclaration) && (typeParamsSet.contains(param.value))) {
                GenericParamDeclaration(TokenDeclaration(param.value))
            } else param
        }
        copy(typeReference = typeReferenceDeclaration.copy(params = params))
    } else this
}

private fun DocumentRootNode.registerTypeAliases(astContext: TypeAliasContext) {
    declarations.forEach { declaration ->
        if (declaration is TypeAliasDeclaration) {
            astContext.registerTypeAlias(declaration.introduceGenericParams())
        } else if (declaration is DocumentRootNode) {
            declaration.registerTypeAliases(astContext)
        }
    }
}

fun DocumentRootNode.lowerTypeAliases(): DocumentRootNode {
    val astContext = TypeAliasContext()
    registerTypeAliases(astContext)
    return LowerTypeAliases(astContext).lowerDocumentRoot(this)
}