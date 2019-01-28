package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

private class LowerTypeAliases(val astContext: AstContext) : ParameterValueLowering {
    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {

        val parentEntitiesRemapped = declaration.parentEntities.map { parent ->
            val resolved = astContext.resolveTypeAlias(parent)

            if (resolved is TypeDeclaration) {
                val iface = InterfaceDeclaration(
                    resolved.value,
                        emptyList(),
                        emptyList(),
                        emptyList()
                )

                iface
            } else {
                parent
            }
        }

        return declaration.copy(parentEntities = parentEntitiesRemapped)
    }
}

fun DocumentRootDeclaration.lowerTypeAliases(astContext: AstContext): DocumentRootDeclaration {
    return LowerTypeAliases(astContext).lowerDocumentRoot(this)
}