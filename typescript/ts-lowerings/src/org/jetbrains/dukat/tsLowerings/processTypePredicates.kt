package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.appendRight
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.TypePredicateDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.expression.AsExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private data class TypePredicateRecord(
    val type: ParameterValueDeclaration,
    val parameterIndex: Int,
    val parameterIsThis: Boolean
)

private data class Replacement(
    val from: ExpressionDeclaration,
    val to: IdentifierExpressionDeclaration
)

private class TypePredicateCollector : DeclarationLowering {

    private val typePredicateFunctions = mutableMapOf<String, TypePredicateRecord>()

    fun findTypePredicate(name: String): TypePredicateRecord? {
        return typePredicateFunctions[name]
    }

    override fun lowerFunctionDeclaration(
        declaration: FunctionDeclaration,
        owner: NodeOwner<FunctionOwnerDeclaration>?
    ): FunctionDeclaration {
        val type = declaration.type
        if (type is TypePredicateDeclaration) {
            if (type.parameter == "this") {
                typePredicateFunctions[declaration.name] = TypePredicateRecord(
                    type.type,
                    0,
                    true
                )
            } else {
                val index = declaration.parameters.indexOfFirst { it.name == type.parameter }
                if (index != -1) {
                    typePredicateFunctions[declaration.name] = TypePredicateRecord(
                        type.type,
                        index,
                        false
                    )
                }
            }
        }
        return super.lowerFunctionDeclaration(declaration, owner)
    }
}

private class TypePredicateExpressionLowering(private val context: TypePredicateCollector) : DeclarationLowering {

    val typePredicateClauses = mutableListOf<Pair<CallExpressionDeclaration, TypePredicateRecord>>()

    override fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        when (declaration) {
            is CallExpressionDeclaration -> {
                val functionName = when (val function = declaration.expression) {
                    is IdentifierExpressionDeclaration -> function.identifier.value
                    is QualifierExpressionDeclaration -> function.qualifier.right.value
                    is PropertyAccessExpressionDeclaration -> function.name.value
                    else -> null
                }
                val typePredicate = functionName?.let { context.findTypePredicate(it) }
                typePredicate?.let {
                    typePredicateClauses += declaration to it
                }
            }
        }
        return super.lower(declaration)
    }
}

private class ReplaceExpressionsLowering(val replacements: Map<ExpressionDeclaration, IdentifierExpressionDeclaration>): DeclarationLowering {
    override fun lowerIdentifier(identifier: IdentifierEntity): IdentifierEntity {
        return replacements[IdentifierExpressionDeclaration(identifier)]?.identifier ?: identifier
    }

    override fun lowerQualifier(qualifier: QualifierEntity): NameEntity {
        return replacements[QualifierExpressionDeclaration(qualifier)]?.identifier ?: super.lowerQualifier(qualifier)
    }

    override fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        return when (declaration) {
            is PropertyAccessExpressionDeclaration -> {
                return replacements[declaration] ?: super.lower(declaration)
            }
            else -> super.lower(declaration)
        }
    }
}

private class TypePredicateLowering(private val context: TypePredicateCollector) : DeclarationLowering {

    private var counter = 1

    private fun createAsExpression(
        checkExpression: CallExpressionDeclaration,
        parameterIndex: Int,
        type: ParameterValueDeclaration,
        parameterIsThis: Boolean
    ): AsExpressionDeclaration? {
        if (parameterIsThis) {
            val callerName = when (val function = checkExpression.expression) {
                is QualifierExpressionDeclaration -> when (val left = function.qualifier.left) {
                    is IdentifierEntity -> IdentifierExpressionDeclaration(left)
                    is QualifierEntity -> QualifierExpressionDeclaration(left)
                }
                is PropertyAccessExpressionDeclaration -> function.expression
                else -> null
            }
            return callerName?.let { caller ->
                AsExpressionDeclaration(
                    expression = caller,
                    type = type
                )
            }
        }
        if (checkExpression.arguments.size <= parameterIndex) {
            return null
        }
        return AsExpressionDeclaration(
            expression = checkExpression.arguments[parameterIndex],
            type = type
        )
    }

    private fun generateVariableName(): String {
        return "_v${counter++}"
    }

    private fun createStatementsAndReplacements(
        checkExpression: CallExpressionDeclaration,
        record: TypePredicateRecord
    ): Pair<StatementDeclaration?, Replacement?> {
        val asExpression = createAsExpression(checkExpression, record.parameterIndex, record.type, record.parameterIsThis)
            ?: return null to null
        return when (asExpression.expression) {
            is IdentifierExpressionDeclaration -> ExpressionStatementDeclaration(asExpression) to null
            else -> {
                val name = generateVariableName()
                VariableDeclaration(
                    name = name,
                    type = TypeDeclaration(IdentifierEntity("Any"), emptyList()),
                    modifiers = setOf(),
                    initializer = asExpression,
                    definitionsInfo = listOf(),
                    uid = "",
                    explicitlyDeclaredType = false
                ) to Replacement(asExpression.expression, IdentifierExpressionDeclaration(IdentifierEntity(name)))
            }
        }
    }

    override fun lowerIfStatement(statement: IfStatementDeclaration): IfStatementDeclaration {
        val expressionLowering = TypePredicateExpressionLowering(context)
        expressionLowering.lower(statement.condition)
        val (newStatements, replacements) = expressionLowering.typePredicateClauses.map { (expression, record) ->
            createStatementsAndReplacements(expression, record)
        }.unzip()
        return super.lowerIfStatement(
            statement.copy(
                thenStatement = BlockDeclaration(
                    newStatements.filterNotNull() +
                            ReplaceExpressionsLowering(
                                replacements.filterNotNull().map { (it.from to it.to) }.toMap()
                            ).lowerBlock(statement.thenStatement).statements
                )
            )
        )
    }
}

class ProcessTypePredicates : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val typePredicateCollector = TypePredicateCollector()
        source.sources.map {
            typePredicateCollector.lowerSourceDeclaration(it.root)
        }

        return source.copy(sources = source.sources.map { sourceFileDeclaration ->
            sourceFileDeclaration.copy(root = TypePredicateLowering(
                typePredicateCollector
            ).lowerSourceDeclaration(sourceFileDeclaration.root))
        })
    }
}