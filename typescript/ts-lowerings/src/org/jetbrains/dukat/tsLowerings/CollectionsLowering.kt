package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NewExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

private class CollectionsLowering : DeclarationStatementLowering() {

    private fun lowerNewExpressionDeclaration(declaration: NewExpressionDeclaration): ExpressionDeclaration {
        val expression = declaration.expression
        if (expression is IdentifierExpressionDeclaration) {
            return when (expression.identifier) {
                IdentifierEntity("Set") -> CallExpressionDeclaration(
                    expression = IdentifierExpressionDeclaration(
                        IdentifierEntity("mutableSetOf")
                    ),
                    arguments = declaration.arguments,
                    typeArguments = declaration.typeArguments
                )
                IdentifierEntity("Array") -> CallExpressionDeclaration(
                    expression = IdentifierExpressionDeclaration(
                        IdentifierEntity("arrayOf")
                    ),
                    arguments = declaration.arguments,
                    typeArguments = declaration.typeArguments
                )
                IdentifierEntity("Map") -> CallExpressionDeclaration(
                    expression = IdentifierExpressionDeclaration(
                        IdentifierEntity("mutableMapOf")
                    ),
                    arguments = declaration.arguments,
                    typeArguments = declaration.typeArguments
                )
                else -> declaration
            }
        }
        return declaration
    }

    override fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        return when (val newDeclaration = super.lower(declaration)) {
            is NewExpressionDeclaration -> lowerNewExpressionDeclaration(newDeclaration)
            else -> newDeclaration
        }
    }
}

class ResolveCollections : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map {
            it.copy(root = CollectionsLowering().lower(it.root))
        })
    }
}