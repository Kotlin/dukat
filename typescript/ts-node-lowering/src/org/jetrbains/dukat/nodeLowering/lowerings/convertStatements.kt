package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.expressions.BinaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.IndexExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.SuperExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.BooleanLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.LiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.NumericLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.StringLiteralExpressionModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.IfStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astModel.statements.WhileStatementModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ElementAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

fun LiteralExpressionDeclaration.convert(): LiteralExpressionModel {
    return when (this) {
        is StringLiteralExpressionDeclaration -> StringLiteralExpressionModel(
            value
        )
        is NumericLiteralExpressionDeclaration -> NumericLiteralExpressionModel(
            value.toInt()
        )
        is BooleanLiteralExpressionDeclaration -> BooleanLiteralExpressionModel(
            value
        )
        else -> raiseConcern("unable to process LiteralExpressionDeclaration ${this}") {
            StringLiteralExpressionModel("ERROR")
        }
    }
}

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
        is LiteralExpressionDeclaration -> this.convert()
        is BinaryExpressionDeclaration -> BinaryExpressionModel(
            left.convert(),
            operator,
            right.convert()
        )
        is UnknownExpressionDeclaration -> when (meta) {
            "this" -> ThisExpressionModel()
            "super" -> SuperExpressionModel()
            else -> raiseConcern("unable to process ExpressionDeclaration ${this}") {
                IdentifierExpressionModel(IdentifierEntity(meta))
            }
        }
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
        is IfStatementDeclaration -> IfStatementModel(
            condition.convert(),
            thenStatement.convert(),
            elseStatement?.convert()
        )
        is WhileStatementDeclaration -> WhileStatementModel(
            condition.convert(),
            statement.convert()
        )
        is BlockDeclaration -> BlockStatementModel(
            statements.map { it.convert() }
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