package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.ForStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration

private class ResolveLoopsLowering : DeclarationStatementLowering {
    override fun lowerExpression(expression: ExpressionDeclaration): ExpressionDeclaration {
        return expression
    }

    fun lowerForStatement(statement: ForStatementDeclaration): List<StatementDeclaration> {
        return listOf(
            BlockDeclaration(
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
            )
        )
    }

    override fun lowerStatement(statement: StatementDeclaration): List<StatementDeclaration> {
        return when (statement) {
            is BlockDeclaration -> listOf(lowerBlockStatement(statement))
            is ForStatementDeclaration -> lowerForStatement(statement)
            else -> listOf(statement)
        }
    }
}

class ResolveLoops : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map {
            it.copy(root = ResolveLoopsLowering().lowerDocumentRoot(it.root))
        })
    }
}