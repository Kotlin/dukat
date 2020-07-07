package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ConditionalExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class ProcessNullabilityChecksLowering : DeclarationLowering {

    private val typeContext = StatementTypeContext()

    private fun ExpressionDeclaration.convertToNullabilityCheck(): ExpressionDeclaration {
        return BinaryExpressionDeclaration(
            left = this,
            operator = "!=",
            right = IdentifierExpressionDeclaration(IdentifierEntity("null"))
        )
    }

    private fun processCondition(condition: ExpressionDeclaration): ExpressionDeclaration {
        if (!typeContext.hasBooleanType(condition)) {
            return condition.convertToNullabilityCheck()
        }
        return condition
    }

    override fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        return when (val newExpression = super.lower(declaration)) {
            is ConditionalExpressionDeclaration -> newExpression.copy(
                condition = processCondition(newExpression.condition)
            )
            else -> newExpression
        }
    }

    override fun lowerIfStatement(statement: IfStatementDeclaration): IfStatementDeclaration {
        val newStatement = super.lowerIfStatement(statement)
        return newStatement.copy(
            condition = processCondition(newStatement.condition)
        )
    }

    override fun lowerWhileStatement(statement: WhileStatementDeclaration): WhileStatementDeclaration {
        val newStatement = super.lowerWhileStatement(statement)
        return newStatement.copy(
            condition = processCondition(newStatement.condition)
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
}

private class StatementTypeContext {

    private data class Scope(
        val variables: MutableList<VariableDeclaration> = mutableListOf()
    )

    private val scopes = mutableListOf<Scope>()

    // if we are not sure, returns true
    fun hasBooleanType(expression: ExpressionDeclaration): Boolean {
        when (expression) {
            is IdentifierExpressionDeclaration -> {
                val registeredVariable = scopes.flatMap { it.variables }.find {
                    it.name == expression.identifier.value
                }
                return registeredVariable?.let {
                    it.type == TypeDeclaration(IdentifierEntity("Boolean"), listOf())
                } ?: true
            }
        }
        return true
    }

    fun registerVariable(variable: VariableDeclaration) {
        scopes.last().variables += variable
    }

    fun startScope() {
        scopes += Scope()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun endScope() {
        scopes.removeLast()
    }
}

class ProcessNullabilityChecks : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFileDeclaration ->
            sourceFileDeclaration.copy(root = ProcessNullabilityChecksLowering().lowerSourceDeclaration(sourceFileDeclaration.root))
        })
    }
}