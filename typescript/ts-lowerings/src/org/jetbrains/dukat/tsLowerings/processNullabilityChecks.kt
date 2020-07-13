package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ConditionalExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ParenthesizedExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration

private class ProcessNullabilityChecksLowering(private val typeContext: StatementTypeContext) : DeclarationLowering {

    private val booleanUnaryOperators = listOf("!")
    private val booleanBinaryOperators = listOf("&&", "||")

    private fun ExpressionDeclaration.convertToNonNullCheck(): ExpressionDeclaration {
        return BinaryExpressionDeclaration(
            left = this,
            operator = "!=",
            right = UnknownExpressionDeclaration("null")
        )
    }

    private fun ExpressionDeclaration.convertToEqualsNullCheck(): ExpressionDeclaration {
        return BinaryExpressionDeclaration(
            left = this,
            operator = "==",
            right = UnknownExpressionDeclaration("null")
        )
    }

    private fun processConditionNonNull(condition: ExpressionDeclaration): ExpressionDeclaration {
        if (!typeContext.hasBooleanType(condition)) {
            return ParenthesizedExpressionDeclaration(condition.convertToNonNullCheck())
        }
        return condition
    }

    private fun processEqualsNull(condition: UnaryExpressionDeclaration): ExpressionDeclaration {
        if (!typeContext.hasBooleanType(condition.operand)) {
            return ParenthesizedExpressionDeclaration(condition.operand.convertToEqualsNullCheck())
        }
        return condition
    }

    override fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        return when (val newExpression = super.lower(declaration)) {
            is ConditionalExpressionDeclaration -> newExpression.copy(
                condition = processConditionNonNull(newExpression.condition)
            )
            is BinaryExpressionDeclaration -> {
                if (booleanBinaryOperators.contains(newExpression.operator)) {
                    newExpression.copy(
                        left = processConditionNonNull(newExpression.left),
                        right = processConditionNonNull(newExpression.right)
                    )
                } else {
                    newExpression
                }
            }
            is UnaryExpressionDeclaration -> {
                if (booleanUnaryOperators.contains(newExpression.operator)) {
                    processEqualsNull(newExpression)
                } else {
                    newExpression
                }
            }
            else -> newExpression
        }
    }

    override fun lowerIfStatement(statement: IfStatementDeclaration): IfStatementDeclaration {
        val newStatement = super.lowerIfStatement(statement)
        return newStatement.copy(
            condition = processConditionNonNull(newStatement.condition)
        )
    }

    override fun lowerWhileStatement(statement: WhileStatementDeclaration): WhileStatementDeclaration {
        val newStatement = super.lowerWhileStatement(statement)
        return newStatement.copy(
            condition = processConditionNonNull(newStatement.condition)
        )
    }

    override fun lowerBlockStatement(block: BlockDeclaration): BlockDeclaration {
        typeContext.startScope()
        val newBlock = super.lowerBlockStatement(block)
        typeContext.endScope()
        return newBlock
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        typeContext.registerVariable(declaration)
        return super.lowerVariableDeclaration(declaration)
    }

    override fun lowerFunctionDeclaration(
        declaration: FunctionDeclaration,
        owner: NodeOwner<FunctionOwnerDeclaration>?
    ): FunctionDeclaration {
        typeContext.startScope()
        declaration.parameters.forEach {
            typeContext.registerParameter(it)
        }
        val newFunction = super.lowerFunctionDeclaration(declaration, owner)
        typeContext.endScope()
        return newFunction
    }
}

class ProcessNullabilityChecks : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val typeContext = StatementTypeContext()
        source.sources.map {
            typeContext.lowerSourceDeclaration(it.root)
        }

        return source.copy(sources = source.sources.map { sourceFileDeclaration ->
            sourceFileDeclaration.copy(root = ProcessNullabilityChecksLowering(
                typeContext
            ).lowerSourceDeclaration(sourceFileDeclaration.root))
        })
    }
}