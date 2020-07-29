package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.expressions.AsExpressionModel
import org.jetbrains.dukat.astModel.expressions.BinaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.ConditionalExpressionModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.IndexExpressionModel
import org.jetbrains.dukat.astModel.expressions.IsExpressionModel
import org.jetbrains.dukat.astModel.expressions.LambdaExpressionModel
import org.jetbrains.dukat.astModel.expressions.NonNullExpressionModel
import org.jetbrains.dukat.astModel.expressions.ParenthesizedExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.UnaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.BooleanLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.NumericLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.StringLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.templates.ExpressionTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.StringTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateExpressionModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateTokenModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.ownerContext.NodeOwner

private val logger = Logging.logger("ModelExpressionLowering")

interface ModelExpressionLowering {
    fun lowerTypeModel(ownerContext: NodeOwner<TypeModel>): TypeModel = ownerContext.node
    fun lower(statement: StatementModel): StatementModel
    fun lowerLambdaParameterModel(ownerContext: NodeOwner<LambdaParameterModel>): LambdaParameterModel = ownerContext.node

    fun lower(expression: ExpressionModel): ExpressionModel {
        return when (expression) {
            is UnaryExpressionModel -> expression.copy(operand = lower(expression.operand))
            is ConditionalExpressionModel -> expression.copy(
                condition = lower(expression.condition),
                whenTrue = lower(expression.whenTrue),
                whenFalse = lower(expression.whenFalse)
            )
            is IndexExpressionModel -> expression.copy(
                array = lower(expression.array),
                index = lower(expression.index)
            )
            is CallExpressionModel -> expression.copy(
                expression = lower(expression.expression),
                arguments = expression.arguments.map { lower(it) },
                typeParameters = expression.typeParameters.map { lowerTypeModel(NodeOwner(it, null)) }
            )
            is PropertyAccessExpressionModel -> expression.copy(
                left = lower(expression.left),
                right = lower(expression.right)
            )
            is LambdaExpressionModel -> expression.copy(
                body = expression.body.copy(
                    statements = expression.body.statements.map { lower(it) }
                ),
                parameters = expression.parameters.map { lowerLambdaParameterModel(NodeOwner(it, null)) }
            )
            is BinaryExpressionModel -> expression.copy(
                left = lower(expression.left),
                right = lower(expression.right)
            )
            is AsExpressionModel -> expression.copy(
                expression = lower(expression.expression),
                type = lowerTypeModel(NodeOwner(expression.type, null))
            )
            is IsExpressionModel -> expression.copy(
                expression = lower(expression.expression),
                type = lowerTypeModel(NodeOwner(expression.type, null))
            )
            is NonNullExpressionModel -> expression.copy(
                expression = lower(expression.expression)
            )
            is ParenthesizedExpressionModel -> expression.copy(
                expression = lower(expression.expression)
            )
            is TemplateExpressionModel -> expression.copy(
                tokens = expression.tokens.map { lowerTemplateToken(it) }
            )
            is BooleanLiteralExpressionModel -> expression
            is NumericLiteralExpressionModel -> expression
            is StringLiteralExpressionModel -> lowerStringLiteralModel(expression)
            is IdentifierExpressionModel -> expression
            else -> {
                logger.debug("[${this}] skipping $expression")
                expression
            }
        }
    }

    fun lowerStringLiteralModel(literal: StringLiteralExpressionModel): StringLiteralExpressionModel {
        return literal
    }

    fun lowerTemplateToken(token: TemplateTokenModel): TemplateTokenModel {
        return when (token) {
            is ExpressionTemplateTokenModel -> token.copy(
                expression = lower(token.expression)
            )
            is StringTemplateTokenModel -> token
            else -> {
                logger.debug("[${this}] skipping $token")
                token
            }
        }
    }

}
