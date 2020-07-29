package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.ArrayDestructuringDeclaration
import org.jetbrains.dukat.tsmodel.BindingElementDeclaration
import org.jetbrains.dukat.tsmodel.BindingVariableDeclaration
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.BreakStatementDeclaration
import org.jetbrains.dukat.tsmodel.CaseDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.ContinueStatementDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.ForOfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ForStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.SwitchStatementDeclaration
import org.jetbrains.dukat.tsmodel.ThrowStatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.VariableLikeDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

interface DeclarationStatementLowering : ExpressionLowering {
    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration

    fun lowerBlock(statement: BlockDeclaration): BlockDeclaration {
        return statement.copy(
                statements = statement.statements.map { lower(it) }
        )
    }

    fun lowerIfStatement(statement: IfStatementDeclaration): IfStatementDeclaration {
        return statement.copy(
                condition = this.lowerExpressionDeclaration(statement.condition),
                thenStatement = this.lowerBlock(statement.thenStatement),
                elseStatement = statement.elseStatement?.let { this.lowerBlock(it) }
        )
    }

    fun lowerThrowStatement(statement: ThrowStatementDeclaration): ThrowStatementDeclaration {
        return statement.copy(expression = this.lowerExpressionDeclaration(statement.expression))
    }

    fun lowerWhileStatement(statement: WhileStatementDeclaration): WhileStatementDeclaration {
        return statement.copy(
                condition = this.lowerExpressionDeclaration(statement.condition),
                statement = this.lowerBlock(statement.statement)
        )
    }

    fun lowerReturnStatement(statement: ReturnStatementDeclaration): ReturnStatementDeclaration {
        return statement.copy(
                expression = statement.expression?.let { this.lowerExpressionDeclaration(it) }
        )
    }

    private fun lowerExpressionDeclaration(expression: ExpressionDeclaration): ExpressionDeclaration {
        return this.lower(expression)
    }

    fun lowerExpression(statement: ExpressionStatementDeclaration): ExpressionStatementDeclaration {
        return statement.copy(
                expression = this.lowerExpressionDeclaration(statement.expression)
        )
    }

    fun lowerForStatementDeclaration(statement: ForStatementDeclaration): StatementDeclaration {
        return statement.copy(
                initializer = this.lowerBlock(statement.initializer),
                condition = statement.condition?.let { this.lowerExpressionDeclaration(it) },
                incrementor = statement.incrementor?.let { this.lowerExpressionDeclaration(it) },
                body = lowerBlock(statement.body)
        )
    }

    fun lowerCase(statement: CaseDeclaration): CaseDeclaration {
        return statement.copy(
                condition = statement.condition?.let { this.lowerExpressionDeclaration(it) },
                body = lowerBlock(statement.body)
        )
    }

    fun lowerSwitchStatementDeclaration(statement: SwitchStatementDeclaration): SwitchStatementDeclaration {
        return statement.copy(
                expression = this.lowerExpressionDeclaration(statement.expression),
                cases = statement.cases.map { lowerCase(it) }
        )
    }

    fun lowerForOfStatementDeclaration(statement: ForOfStatementDeclaration): ForOfStatementDeclaration {
        return statement.copy(
            variable = this.lowerVariableLikeDeclaration(statement.variable),
            expression = this.lowerExpressionDeclaration(statement.expression),
            body = lowerBlock(statement.body)
        )
    }

    fun lowerVariableLikeDeclaration(statement: VariableLikeDeclaration): VariableLikeDeclaration {
        return when (statement) {
            is VariableDeclaration -> lowerVariableDeclaration(statement)
            is ArrayDestructuringDeclaration -> lowerArrayDestructuringDeclaration(statement)
            else -> statement
        }
    }

    fun lowerBindingVariableDeclaration(statement: BindingVariableDeclaration): BindingVariableDeclaration {
        return statement.copy(initializer = statement.initializer?.let { lowerExpressionDeclaration(it) })
    }

    fun lowerArrayDestructuringDeclaration(statement: ArrayDestructuringDeclaration): ArrayDestructuringDeclaration {
        return statement.copy(elements = statement.elements.map { lowerBindingElementDeclaration(it) })
    }

    fun lowerBindingElementDeclaration(statement: BindingElementDeclaration): BindingElementDeclaration {
        return when (statement) {
            is BindingVariableDeclaration -> lowerBindingVariableDeclaration(statement)
            is ArrayDestructuringDeclaration -> lowerArrayDestructuringDeclaration(statement)
            else -> statement
        }
    }

    override fun lower(statement: StatementDeclaration): StatementDeclaration {
        return when (statement) {
            is BlockDeclaration -> lowerBlock(statement)
            is IfStatementDeclaration -> lowerIfStatement(statement)
            is ThrowStatementDeclaration -> lowerThrowStatement(statement)
            is VariableDeclaration -> this.lowerVariableDeclaration(statement)
            is WhileStatementDeclaration -> lowerWhileStatement(statement)
            is FunctionDeclaration -> statement.copy(body = statement.body?.let { lowerBlock(it) } )
            is ReturnStatementDeclaration -> lowerReturnStatement(statement)
            is ExpressionStatementDeclaration -> lowerExpression(statement)
            is ForStatementDeclaration -> lowerForStatementDeclaration(statement)
            is SwitchStatementDeclaration -> lowerSwitchStatementDeclaration(statement)
            is ContinueStatementDeclaration -> statement
            is BreakStatementDeclaration -> statement
            is ForOfStatementDeclaration -> lowerForOfStatementDeclaration(statement)
            else -> statement
        }
    }

}