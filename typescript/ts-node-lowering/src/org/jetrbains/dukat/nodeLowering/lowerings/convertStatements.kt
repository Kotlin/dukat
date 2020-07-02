package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.convertToNode
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
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
import org.jetbrains.dukat.astModel.expressions.SuperExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.expressions.UnaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.BooleanLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.NullLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.NumericLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.StringLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.AND
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.ASSIGN
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.BITWISE_AND
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.BITWISE_OR
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.BITWISE_XOR
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.DIV
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.DIV_ASSIGN
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.EQ
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.GE
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.GT
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.LE
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.LT
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.MINUS
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.MINUS_ASSIGN
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.MOD
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.MOD_ASSIGN
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.MULT
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.MULT_ASSIGN
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.NOT_EQ
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.OR
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.PLUS
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.PLUS_ASSIGN
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.REF_EQ
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.REF_NOT_EQ
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.SHIFT_LEFT
import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel.SHIFT_RIGHT
import org.jetbrains.dukat.astModel.expressions.operators.UnaryOperatorModel
import org.jetbrains.dukat.astModel.expressions.operators.UnaryOperatorModel.DECREMENT
import org.jetbrains.dukat.astModel.expressions.operators.UnaryOperatorModel.INCREMENT
import org.jetbrains.dukat.astModel.expressions.operators.UnaryOperatorModel.NOT
import org.jetbrains.dukat.astModel.expressions.operators.UnaryOperatorModel.UNARY_MINUS
import org.jetbrains.dukat.astModel.expressions.operators.UnaryOperatorModel.UNARY_PLUS
import org.jetbrains.dukat.astModel.expressions.templates.ExpressionTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.StringTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateExpressionModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateTokenModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.BreakStatementModel
import org.jetbrains.dukat.astModel.statements.CaseModel
import org.jetbrains.dukat.astModel.statements.ContinueStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ForInStatementModel
import org.jetbrains.dukat.astModel.statements.IfStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.RunBlockStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astModel.statements.ThrowStatementModel
import org.jetbrains.dukat.astModel.statements.WhenStatementModel
import org.jetbrains.dukat.astModel.statements.WhileStatementModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ArrayDestructuringDeclaration
import org.jetbrains.dukat.tsmodel.BindingVariableDeclaration
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.BreakStatementDeclaration
import org.jetbrains.dukat.tsmodel.CaseDeclaration
import org.jetbrains.dukat.tsmodel.ContinueStatementDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.ForOfStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.SwitchStatementDeclaration
import org.jetbrains.dukat.tsmodel.ThrowStatementDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
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
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.YieldExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ArrayLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.ExpressionTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.StringTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

class ExpressionConverter(private val typeConverter: (TypeNode) -> TypeModel) {
    private fun LiteralExpressionDeclaration.convert(): ExpressionModel {
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
            is ArrayLiteralExpressionDeclaration -> CallExpressionModel(
                IdentifierExpressionModel(IdentifierEntity("arrayOf")),
                elements.map { it.convert() }
            )
            else -> raiseConcern("unable to process LiteralExpressionDeclaration $this") {
                StringLiteralExpressionModel("ERROR")
            }
        }
    }

    private fun TemplateTokenDeclaration.convert(): TemplateTokenModel {
        return when (this) {
            is StringTemplateTokenDeclaration -> StringTemplateTokenModel(value.value)
            is ExpressionTemplateTokenDeclaration -> ExpressionTemplateTokenModel(expression.convert())
            else -> raiseConcern("unable to process TemplateTokenDeclaration $this") {
                StringTemplateTokenModel("ERROR")
            }
        }
    }

    private fun convertBinaryOperator(operator: String): BinaryOperatorModel {
        return when (operator) {
            "+" -> PLUS
            "-" -> MINUS
            "*" -> MULT
            "/" -> DIV
            "%" -> MOD
            "=" -> ASSIGN
            "+=" -> PLUS_ASSIGN
            "-=" -> MINUS_ASSIGN
            "*=" -> MULT_ASSIGN
            "/=" -> DIV_ASSIGN
            "%=" -> MOD_ASSIGN
            "&&" -> AND
            "||" -> OR
            "==" -> EQ
            "!=" -> NOT_EQ
            "===" -> REF_EQ
            "!==" -> REF_NOT_EQ
            "<" -> LT
            ">" -> GT
            "<=" -> LE
            ">=" -> GE
            "&" -> BITWISE_AND
            "|" -> BITWISE_OR
            "^" -> BITWISE_XOR
            "<<" -> SHIFT_LEFT
            ">>" -> SHIFT_RIGHT
            else -> raiseConcern("unable to process binary operator $this") {
                PLUS
            }
        }
    }

    private fun convertUnaryOperator(operator: String): UnaryOperatorModel {
        return when (operator) {
            "++" -> INCREMENT
            "--" -> DECREMENT
            "+" -> UNARY_PLUS
            "-" -> UNARY_MINUS
            "!" -> NOT
            else -> raiseConcern("unable to process unary operator $this") {
                INCREMENT
            }
        }
    }

    private fun ExpressionDeclaration.convert(): ExpressionModel {
        return when (this) {
            is IdentifierExpressionDeclaration -> IdentifierExpressionModel(
                identifier
            )
            is QualifierExpressionDeclaration -> {
                IdentifierExpressionModel(
                    qualifier
                )
            }
            is CallExpressionDeclaration -> CallExpressionModel(
                expression.convert(),
                arguments.map { it.convert() },
                typeArguments.map {
                    it.convert()
                }
            )
            is FunctionDeclaration -> LambdaExpressionModel(
                parameters.map {
                    LambdaParameterModel(
                        it.name,
                        it.type.convert(),
                        it.hasType
                    )
                },
                convertLambdaBody(body!!)
            )
            is NewExpressionDeclaration -> CallExpressionModel(
                expression.convert(),
                arguments.map { it.convert() },
                typeArguments.map {
                    it.convert()
                }
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
            is BinaryExpressionDeclaration -> this.convert()
            is UnaryExpressionDeclaration -> UnaryExpressionModel(
                operand.convert(),
                convertUnaryOperator(operator),
                isPrefix
            )
            is ConditionalExpressionDeclaration -> ConditionalExpressionModel(
                condition.convert(),
                whenTrue.convert(),
                whenFalse.convert()
            )
            is AsExpressionDeclaration -> AsExpressionModel(
                expression.convert(),
                type.convert()
            )
            is NonNullExpressionDeclaration -> NonNullExpressionModel(
                expression.convert()
            )
            is YieldExpressionDeclaration -> CallExpressionModel(
                expression = IdentifierExpressionModel(IdentifierEntity(
                    if (hasAsterisk) "yieldAll" else "yield"
                )),
                arguments = listOf(expression?.convert() ?: NullLiteralExpressionModel())
            )
            is ParenthesizedExpressionDeclaration -> ParenthesizedExpressionModel(
                expression = expression.convert()
            )
            is UnknownExpressionDeclaration -> when (meta) {
                "this" -> ThisExpressionModel()
                "super" -> SuperExpressionModel()
                else -> raiseConcern("unable to process ExpressionDeclaration $this") {
                    IdentifierExpressionModel(IdentifierEntity(meta))
                }
            }
            else -> raiseConcern("unable to process ExpressionDeclaration $this") {
                IdentifierExpressionModel(IdentifierEntity("ERROR"))
            }
        }
    }

    private fun BlockDeclaration.isFallthroughBlock(): Boolean {
        return when (statements.lastOrNull()) {
            is BreakStatementDeclaration -> false
            else -> true
        }
    }

    private fun SwitchStatementDeclaration.splitToFallthroughBlocks(): List<List<CaseDeclaration>> {
        val fallthroughBlocks = mutableListOf<List<CaseDeclaration>>()
        var currentBlock = mutableListOf<CaseDeclaration>()
        cases.forEach {
            currentBlock.add(it)
            if (!it.body.isFallthroughBlock()) {
                fallthroughBlocks += currentBlock
                currentBlock = mutableListOf()
            }
        }
        if (currentBlock.isNotEmpty()) {
            fallthroughBlocks += currentBlock
        }
        return fallthroughBlocks
    }

    private fun StatementModel.removeBreaks(): StatementModel? {
        return when (this) {
            is BlockStatementModel -> removeBreaks()
            is BreakStatementModel -> null
            is IfStatementModel -> copy(
                thenStatement = thenStatement.removeBreaks(),
                elseStatement = elseStatement?.removeBreaks()
            )
            is RunBlockStatementModel -> copy(statements = statements.mapNotNull { it.removeBreaks() })
            is WhenStatementModel -> copy(cases = cases.map { it.copy(body = it.body.removeBreaks()) })
            is WhileStatementModel -> copy(body = body.removeBreaks())
            else -> this
        }
    }

    private fun BlockStatementModel.removeBreaks() = copy(statements = statements.mapNotNull { it.removeBreaks() })

    private fun List<CaseDeclaration>.convert(expressionToCompare: ExpressionModel): CaseModel? {
        return when (this.size) {
            0 -> null
            1 -> CaseModel(
                first().condition?.convert()?.let { listOf(it) },
                convertBlock(first().body)
            )
            else -> {
                val condition = if (none { it.condition == null }) {
                    mapNotNull { it.condition?.convert() }
                } else {
                    null
                }
                val body = map { case ->
                    if (case.condition == null) {
                        case.body.statements.map { it.convert() }
                    } else {
                        listOf(
                            IfStatementModel(
                                BinaryExpressionModel(
                                    expressionToCompare,
                                    EQ,
                                    case.condition!!.convert()
                                ),
                                convertBlock(case.body),
                                null
                            )
                        )
                    }
                }.flatten()
                CaseModel(
                    condition,
                    BlockStatementModel(body)
                )
            }
        }
    }

    private fun SwitchStatementDeclaration.convert(): WhenStatementModel {
        val expressionToCompare = expression.convert()

        return WhenStatementModel(
            expressionToCompare,
            splitToFallthroughBlocks()
                .mapNotNull {
                    val caseModel = it.convert(expressionToCompare)
                    caseModel?.copy(body = caseModel.body.removeBreaks())
                }
        )
    }

    private fun ForOfStatementDeclaration.convert(): StatementModel {
        return when (variable) {
            is VariableDeclaration -> ForInStatementModel(
                variable.convert() as VariableModel,
                expression.convert(),
                convertBlock(body)
            )
            is ArrayDestructuringDeclaration -> {
                val fictiveVariable = VariableDeclaration(
                    name = "_i",
                    type = TypeDeclaration(
                        value = IdentifierEntity("Any"),
                        params = listOf()
                    ),
                    modifiers = setOf(),
                    initializer = null,
                    definitionsInfo = listOf(),
                    uid = "",
                    hasType = false
                )
                val newAssignments = (variable as ArrayDestructuringDeclaration).elements.mapIndexedNotNull { index, element ->
                    if (index < 2 && element is BindingVariableDeclaration) {
                        VariableDeclaration(
                            name = element.name,
                            type = TypeDeclaration(
                                value = IdentifierEntity("Any"),
                                params = listOf()
                            ),
                            modifiers = setOf(),
                            initializer = QualifierExpressionDeclaration(
                                QualifierEntity(
                                    IdentifierEntity(fictiveVariable.name),
                                    IdentifierEntity(when (index) {
                                        0 -> "key"
                                        1 -> "value"
                                        else -> raiseConcern("invalid index in $element") { "" }
                                    })
                                )
                            ),
                            definitionsInfo = listOf(),
                            uid = "",
                            hasType = false
                        )
                    } else {
                        // TODO: nested destructuring
                        // TODO: not only key/value destructuring, but also normal arrays
                        null
                    }
                }
                ForInStatementModel(
                    fictiveVariable.convert(),
                    expression.convert(),
                    convertBlock( body.copy(
                        statements = newAssignments + body.statements
                    ))
                )
            }
            else -> raiseConcern("unable to process ForOfStatementDeclaration $this") {
                ExpressionStatementModel(
                    IdentifierExpressionModel(IdentifierEntity("ERROR"))
                )
            }
        }
    }

    private fun ParameterValueDeclaration.convert(): TypeModel {
        return typeConverter(this.convertToNode())
    }

    private fun VariableDeclaration.convert(): VariableModel {
        return VariableModel(
                name = IdentifierEntity(name),
                type = type.convert(),
                annotations = mutableListOf(),
                immutable = false,
                inline = false,
                external = false,
                initializer = initializer?.convert(),
                get = null,
                set = null,
                typeParameters = listOf(),
                extend = null,
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = null,
                hasType = hasType
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
            is ThrowStatementDeclaration -> ThrowStatementModel(
                expression.convert()
            )
            is BreakStatementDeclaration -> BreakStatementModel()
            is ContinueStatementDeclaration -> ContinueStatementModel()
            is IfStatementDeclaration -> IfStatementModel(
                condition.convert(),
                convertBlock(thenStatement),
                elseStatement?.let { convertBlock(it) }
            )
            is WhileStatementDeclaration -> WhileStatementModel(
                condition.convert(),
                convertBlock(statement)
            )
            is ForOfStatementDeclaration -> convert()
            is SwitchStatementDeclaration -> convert()
            is BlockDeclaration -> RunBlockStatementModel(
                statements.map { it.convert() }
            )
            is VariableDeclaration -> convert()
            else -> raiseConcern("unable to process StatementDeclaration $this") {
                ExpressionStatementModel(
                    IdentifierExpressionModel(IdentifierEntity("ERROR"))
                )
            }
        }
    }

    fun convertBlock(blockDeclaration: BlockDeclaration): BlockStatementModel {
        return BlockStatementModel(blockDeclaration.statements.map { it.convert() })
    }

    private fun BlockDeclaration.countReturns(): Int {
        return statements.sumBy { when (it) {
            is ReturnStatementDeclaration -> if (it.expression == null) {
                2
            } else {
                1
            }
            is IfStatementDeclaration -> {
                it.thenStatement.countReturns() + (it.elseStatement?.countReturns() ?: 0)
            }
            is WhileStatementDeclaration -> it.statement.countReturns()
            is ForOfStatementDeclaration -> it.body.countReturns()
            is SwitchStatementDeclaration -> it.cases.sumBy { case -> case.body.countReturns() }
            is BlockDeclaration -> it.countReturns()
            else -> 0
        } }
    }

    private fun BlockDeclaration.removeReturns(): BlockDeclaration {
        return BlockDeclaration(statements.map {
            when (it) {
                is ReturnStatementDeclaration -> {
                    it.expression?.let { e -> ExpressionStatementDeclaration(e) } ?: it
                }
                is IfStatementDeclaration -> {
                    it.copy(
                        thenStatement = it.thenStatement.removeReturns(),
                        elseStatement = it.elseStatement?.removeReturns()
                    )
                }
                is WhileStatementDeclaration -> it.copy(
                    statement = it.statement.removeReturns()
                )
                is ForOfStatementDeclaration -> it.copy(
                    body = it.body.removeReturns()
                )
                is SwitchStatementDeclaration -> it.copy(
                    cases = it.cases.map { case -> case.copy(body = case.body.removeReturns()) }
                )
                is BlockDeclaration -> it.removeReturns()
                else -> it
            }
        })
    }

    private fun convertLambdaBody(lambdaBody: BlockDeclaration): BlockStatementModel {
        if (lambdaBody.countReturns() <= 1) {
            return convertBlock(lambdaBody.removeReturns())
        }
        return raiseConcern("multi return lambdas are not supported yet") {
            BlockStatementModel(
                listOf(
                    ExpressionStatementModel(
                        IdentifierExpressionModel(
                            IdentifierEntity(
                                "MULTI_RETURN_LAMBDAS_ARE_NOT_SUPPORTED_YET"
                            )
                        )
                    )
                )
            )
        }
    }

    private fun convertTypeOf(expression: ExpressionModel, type: String): ExpressionModel {
        return IsExpressionModel(
            expression = expression,
            type = TypeValueModel(
                IdentifierEntity(
                    when (type) {
                        "boolean" -> "Boolean"
                        "number" -> "Number"
                        "string" -> "String"
                        else -> raiseConcern("unsupported type in typeof: $type") {
                            "UNSUPPORTED_TYPE"
                        }
                    }),
                listOf(),
                null,
                null
            )
        )
    }

    private fun BinaryExpressionDeclaration.convert(): ExpressionModel {
        if (operator == "instanceof") {
            val right = right
            return IsExpressionModel(
                expression = left.convert(),
                type = if (right is IdentifierExpressionDeclaration) {

                    // TODO: determine if rhs of instanceof demands type paramaters
                    val params = if (right.identifier.value == "Map") {
                        val star = TypeParameterModel(
                            type = TypeValueModel(
                                value = IdentifierEntity("*"),
                                params = listOf(),
                                metaDescription = null,
                                fqName = null
                            ),
                            constraints = listOf()
                        )
                        listOf(star, star.duplicate())
                    } else {
                        listOf()
                    }

                    TypeValueModel(
                        value = right.identifier,
                        params = params,
                        metaDescription = null,
                        fqName = null
                    )
                } else {
                    // TODO: support cases when rhs is not identifier
                    TypeValueModel(
                        value = IdentifierEntity("UNSUPPORTED_TYPE"),
                        params = listOf(),
                        metaDescription = null,
                        fqName = null
                    )
                }
            )
        }
        if (convertBinaryOperator(operator) == EQ) {
            val left = left
            val right = right
            when {
                left is TypeOfExpressionDeclaration && right is StringLiteralExpressionDeclaration -> {
                    return convertTypeOf(left.expression.convert(), right.value)
                }
                right is TypeOfExpressionDeclaration && left is StringLiteralExpressionDeclaration -> {
                    return convertTypeOf(right.expression.convert(), left.value)
                }
            }
        }
        return BinaryExpressionModel(
            left.convert(),
            convertBinaryOperator(operator),
            right.convert()
        )
    }

    fun convertExpression(expression: ExpressionDeclaration?): ExpressionModel? {
        return expression?.convert()
    }
}