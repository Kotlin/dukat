package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MemberOwnerDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.expression.AsExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ConditionalExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ElementAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NewExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NonNullExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ParenthesizedExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.SpreadExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.YieldExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ArrayLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ObjectLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.RegExLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.ExpressionTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.StringTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private val logger = Logging.logger("ExpressionLowering")

interface ExpressionLowering {
    fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration, owner: NodeOwner<Declaration>?): TypeParameterDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterDeclaration

    fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        return when (declaration) {
            is UnaryExpressionDeclaration -> declaration.copy(operand = lower(declaration.operand))
            is ConditionalExpressionDeclaration -> declaration.copy(
                    condition = lower(declaration.condition),
                    whenTrue = lower(declaration.whenTrue),
                    whenFalse = lower(declaration.whenFalse)
            )
            is ElementAccessExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression),
                    argumentExpression = lower(declaration.argumentExpression)
            )
            is UnknownExpressionDeclaration -> declaration
            is CallExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression),
                    arguments = declaration.arguments.map { lower(it) },
                    typeArguments = declaration.typeArguments.map { lowerParameterValue(it, null) }
            )
            is PropertyAccessExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression)
            )
            is FunctionDeclaration -> declaration.copy(
                    body = declaration.body?.copy(
                        statements = declaration.body!!.statements.map { lower(it) }
                    ),
                    type = lowerParameterValue(declaration.type, null),
                    typeParameters = declaration.typeParameters.map { lowerTypeParameter(it, null) },
                    parameters = declaration.parameters.map { lowerParameterDeclaration(it, null) }
            )
            is BinaryExpressionDeclaration -> declaration.copy(
                    left = lower(declaration.left),
                    right = lower(declaration.right)
            )
            is TypeOfExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression)
            )
            is NewExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression),
                    arguments = declaration.arguments.map { lower(it) },
                    typeArguments = declaration.typeArguments.map { lowerParameterValue(it, null) }
            )
            is AsExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression),
                    type = lowerParameterValue(declaration.type, null)
            )
            is NonNullExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression)
            )
            is YieldExpressionDeclaration -> declaration.copy(
                    expression = declaration.expression?.let { lower(it) }
            )
            is SpreadExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression)
            )
            is ParenthesizedExpressionDeclaration -> declaration.copy(
                    expression = lower(declaration.expression)
            )
            is TemplateExpressionDeclaration -> declaration.copy(
                    tokens = declaration.tokens.map { lowerTemplateToken(it) }
            )
            is ArrayLiteralExpressionDeclaration -> declaration.copy(
                    elements = declaration.elements.map { lower(it) }
            )
            is ObjectLiteralExpressionDeclaration -> declaration.copy(
                    members = declaration.members.map { lowerMemberDeclaration(it, null) }
            )
            is BigIntLiteralExpressionDeclaration -> declaration
            is BooleanLiteralExpressionDeclaration -> declaration
            is NumericLiteralExpressionDeclaration -> declaration
            is RegExLiteralExpressionDeclaration -> declaration
            is StringLiteralExpressionDeclaration -> lowerStringLiteralDeclaration(declaration)
            is IdentifierExpressionDeclaration -> declaration
            is QualifierExpressionDeclaration -> declaration
            else -> {
                logger.debug("[${this}] skipping $declaration")
                declaration
            }
        }
    }

    fun lowerStringLiteralDeclaration(literal: StringLiteralExpressionDeclaration): StringLiteralExpressionDeclaration {
        return literal
    }

    fun lowerTemplateToken(token: TemplateTokenDeclaration): TemplateTokenDeclaration {
        return when (token) {
            is ExpressionTemplateTokenDeclaration -> token.copy(
                expression = lower(token.expression)
            )
            is StringTemplateTokenDeclaration -> token.copy(
                value = lowerStringLiteralDeclaration(token.value)
            )
            else -> {
                logger.debug("[${this}] skipping $token")
                token
            }
        }
    }

    fun lower(statement: StatementDeclaration): StatementDeclaration

    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: NodeOwner<MemberOwnerDeclaration>?): MemberDeclaration
}