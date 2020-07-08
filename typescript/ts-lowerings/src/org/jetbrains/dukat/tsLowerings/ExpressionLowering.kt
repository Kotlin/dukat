package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
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
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.SpreadExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.YieldExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

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
            else -> declaration
        }
    }

    fun lower(statement: StatementDeclaration): StatementDeclaration
}