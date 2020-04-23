package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.statements.WhenStatementModel
import org.jetbrains.dukat.astModel.expressions.AsExpressionModel
import org.jetbrains.dukat.astModel.expressions.BinaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.ConditionalExpressionModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.IndexExpressionModel
import org.jetbrains.dukat.astModel.expressions.NonNullExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.SuperExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.expressions.UnaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.BooleanLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.LiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.NumericLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.StringLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.templates.ExpressionTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.StringTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateExpressionModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateTokenModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.CaseModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.IfStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.RunBlockStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astModel.statements.WhileStatementModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.SwitchStatementDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.AsExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ConditionalExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ElementAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NewExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NonNullExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.ExpressionTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.StringTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateTokenDeclaration

internal class ExpressionConverter(val documentConverter: DocumentConverter) {
    private fun LiteralExpressionDeclaration.convert(): LiteralExpressionModel {
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

    private fun TemplateTokenDeclaration.convert(): TemplateTokenModel {
        return when (this) {
            is StringTemplateTokenDeclaration -> StringTemplateTokenModel(value.convert() as StringLiteralExpressionModel)
            is ExpressionTemplateTokenDeclaration -> ExpressionTemplateTokenModel(expression.convert())
            else -> raiseConcern("unable to process TemplateTokenDeclaration ${this}") {
                StringTemplateTokenModel(StringLiteralExpressionModel("ERROR"))
            }
        }
    }

    private fun ExpressionDeclaration.convert(): ExpressionModel {
        return when (this) {
            is IdentifierExpressionDeclaration -> IdentifierExpressionModel(
                identifier
            )
            is CallExpressionDeclaration -> CallExpressionModel(
                expression.convert(),
                arguments.map { it.convert() }
            )
            is NewExpressionDeclaration -> CallExpressionModel(
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
            is TemplateExpressionDeclaration -> TemplateExpressionModel(
                tokens.map { it.convert() }
            )
            is BinaryExpressionDeclaration -> BinaryExpressionModel(
                left.convert(),
                operator,
                right.convert()
            )
            is UnaryExpressionDeclaration -> UnaryExpressionModel(
                operand.convert(),
                operator,
                isPrefix
            )
            is ConditionalExpressionDeclaration -> ConditionalExpressionModel(
                condition.convert(),
                whenTrue.convert(),
                whenFalse.convert()
            )
            is AsExpressionDeclaration -> AsExpressionModel(
                expression.convert(),
                TypeValueModel(
                    IdentifierEntity("String"),
                    listOf(),
                    null,
                    null
                )
                //TODO
                /*with (documentConverter) {
                    type.process()
                }*/
            )
            is NonNullExpressionDeclaration -> NonNullExpressionModel(
                expression.convert()
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

    private fun SwitchStatementDeclaration.convert(): WhenStatementModel {
        return WhenStatementModel(
            expression.convert(),
            cases.map { CaseModel(
                it.condition?.convert(),
                convertBlock(it.body)
            ) }
        )
    }

    private fun StatementDeclaration.convert(): StatementModel {
        return when (this) {
            is ExpressionStatementDeclaration -> ExpressionStatementModel(
                expression.convert()
            )
            is ReturnStatementDeclaration -> ReturnStatementModel(
                expression?.convert()
            )
            is IfStatementDeclaration -> IfStatementModel(
                condition.convert(),
                convertBlock(thenStatement),
                elseStatement?.let { convertBlock(it) }
            )
            is WhileStatementDeclaration -> WhileStatementModel(
                condition.convert(),
                convertBlock(statement)
            )
            is SwitchStatementDeclaration -> convert()
            is BlockDeclaration -> RunBlockStatementModel(
                statements.map { it.convert() }
            )
            is VariableDeclaration -> VariableModel(
                name = IdentifierEntity(name),
                type = TypeValueModel(
                    IdentifierEntity("Number"),
                    listOf(),
                    null,
                    null
                )
                //TODO
                /*with (documentConverter) {
                    type.process()
                }*/,
                annotations = mutableListOf(),
                immutable = false,
                inline = false,
                external = false,
                initializer = initializer?.convert()?.let {
                    ExpressionStatementModel(it)
                },
                get = null,
                set = null,
                typeParameters = listOf(),
                extend = null,
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = null
            )
            else -> raiseConcern("unable to process StatementDeclaration ${this}") {
                ExpressionStatementModel(
                    IdentifierExpressionModel(IdentifierEntity("ERROR"))
                )
            }
        }
    }

    fun convertBlock(blockDeclaration: BlockDeclaration): BlockStatementModel {
        return BlockStatementModel(blockDeclaration.statements.mapNotNull { it.convert() })
    }
}