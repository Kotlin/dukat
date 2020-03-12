package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.IndexExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.StringLiteralExpressionModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ElementAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

fun ExpressionDeclaration.convert(): ExpressionModel {
    return when (this) {
        is IdentifierExpressionDeclaration -> IdentifierExpressionModel(
            identifier
        )
        is CallExpressionDeclaration -> CallExpressionModel(
            expression.convert(),
            arguments.map { it.convert() }
        )
        is PropertyAccessExpressionDeclaration -> PropertyAccessExpressionModel(
            expression.convert(),
            IdentifierExpressionModel(name)
        )
        is ElementAccessExpressionDeclaration -> IndexExpressionModel(
            expression.convert(),
            argumentExpression.convert()
        )
        is StringLiteralExpressionDeclaration -> StringLiteralExpressionModel(
            value
        )
        else -> raiseConcern("unable to process ExpressionDeclaration ${this}") {
            IdentifierExpressionModel(IdentifierEntity("ERROR"))
        }
    }
}

fun StatementDeclaration.convert(): StatementModel {
    return when (this) {
        is ExpressionStatementDeclaration -> ExpressionStatementModel(
            expression.convert()
        )
        is ReturnStatementDeclaration -> ReturnStatementModel(
            expression?.convert()
        )
        else -> raiseConcern("unable to process StatementDeclaration ${this}") {
            ExpressionStatementModel(
                IdentifierExpressionModel(IdentifierEntity("ERROR"))
            )
        }
    }
}

fun BlockDeclaration.convert(): List<StatementModel> {
    return statements.mapNotNull { it.convert() }
}