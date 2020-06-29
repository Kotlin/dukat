package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.ForStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration

private class ResolveLoopsLowering : DeclarationLowering {

    override fun lowerForStatementDeclaration(statement: ForStatementDeclaration): StatementDeclaration {
        return lowerBlock(BlockDeclaration(
                statement.initializer.statements +
                        WhileStatementDeclaration(
                                statement.condition ?: BooleanLiteralExpressionDeclaration(true),
                                BlockDeclaration(
                                        statement.body.statements +
                                                (statement.incrementor?.let {
                                                    listOf(ExpressionStatementDeclaration(it))
                                                } ?: emptyList())
                                )
                        )
        ))
    }
}

class ResolveLoops : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map {
            it.copy(root = ResolveLoopsLowering().lowerSourceDeclaration(it.root))
        })
    }
}