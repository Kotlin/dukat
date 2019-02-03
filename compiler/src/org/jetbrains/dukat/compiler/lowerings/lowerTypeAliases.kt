package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.compiler.AstContext
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class LowerTypeAliases(val astContext: AstContext) : ParameterValueLowering {
    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {

        val parentEntitiesRemapped = declaration.parentEntities.map { parent ->
            val resolved = astContext.resolveTypeAlias(parent)

            if (resolved is TypeDeclaration) {
                HeritageClauseDeclaration(resolved.value, emptyList(), false)
            } else {
                parent
            }
        }

        return declaration.copy(parentEntities = parentEntitiesRemapped)
    }

    override fun lowerVariableNode(declaration: VariableNode): VariableNode {
        val resolved = astContext.resolveTypeAlias(declaration.type)
        val type = if (resolved is TypeDeclaration) {
            resolved
        } else {
            declaration.type
        }

        return declaration.copy(type = type)
    }
}

fun DocumentRootNode.lowerTypeAliases(astContext: AstContext): DocumentRootNode {
    return LowerTypeAliases(astContext).lowerDocumentRoot(this)
}