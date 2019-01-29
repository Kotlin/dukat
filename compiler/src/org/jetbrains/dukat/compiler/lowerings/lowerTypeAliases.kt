package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.HeritageClauseDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

private class LowerTypeAliases(val astContext: AstContext) : ParameterValueLowering {
    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {

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

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        val resolved = astContext.resolveTypeAlias(declaration.type)
        val type = if (resolved is TypeDeclaration) {
            resolved
        } else {
            declaration.type
        }

        return declaration.copy(type = type)
    }
}

fun DocumentRootDeclaration.lowerTypeAliases(astContext: AstContext): DocumentRootDeclaration {
    return LowerTypeAliases(astContext).lowerDocumentRoot(this)
}