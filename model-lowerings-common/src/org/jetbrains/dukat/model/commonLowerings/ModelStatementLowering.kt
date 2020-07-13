package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.BreakStatementModel
import org.jetbrains.dukat.astModel.statements.CaseModel
import org.jetbrains.dukat.astModel.statements.ContinueStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ForInStatementModel
import org.jetbrains.dukat.astModel.statements.IfStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astModel.statements.ThrowStatementModel
import org.jetbrains.dukat.astModel.statements.WhenStatementModel
import org.jetbrains.dukat.astModel.statements.WhileStatementModel
import org.jetbrains.dukat.ownerContext.NodeOwner

interface ModelStatementLowering : ModelExpressionLowering {
    fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel?): VariableModel = ownerContext.node

    fun lowerBlock(statement: BlockStatementModel): BlockStatementModel {
        return statement.copy(
            statements = statement.statements.map { lower(it) }
        )
    }

    fun lowerAssignmentStatement(statement: AssignmentStatementModel): AssignmentStatementModel {
        return statement.copy(
            left = lower(statement.left),
            right = lower(statement.right)
        )
    }

    fun lowerIfStatement(statement: IfStatementModel): IfStatementModel {
        return statement.copy(
            condition = this.lowerExpressionModel(statement.condition),
            thenStatement = this.lowerBlock(statement.thenStatement),
            elseStatement = statement.elseStatement?.let { this.lowerBlock(it) }
        )
    }

    fun lowerThrowStatement(statement: ThrowStatementModel): ThrowStatementModel {
        return statement.copy(expression = this.lowerExpressionModel(statement.expression))
    }

    fun lowerWhileStatement(statement: WhileStatementModel): WhileStatementModel {
        return statement.copy(
            condition = this.lowerExpressionModel(statement.condition),
            body = this.lowerBlock(statement.body)
        )
    }

    fun lowerReturnStatement(statement: ReturnStatementModel): ReturnStatementModel {
        return statement.copy(
            expression = statement.expression?.let { this.lowerExpressionModel(it) }
        )
    }

    private fun lowerExpressionModel(expression: ExpressionModel): ExpressionModel {
        return this.lower(expression)
    }

    fun lowerExpression(statement: ExpressionStatementModel): ExpressionStatementModel {
        return statement.copy(
            expression = this.lowerExpressionModel(statement.expression)
        )
    }

    fun lowerCase(statement: CaseModel): CaseModel {
        return statement.copy(
            condition = statement.condition?.map { this.lowerExpressionModel(it) },
            body = lowerBlock(statement.body)
        )
    }

    fun lowerWhenStatementModel(statement: WhenStatementModel): WhenStatementModel {
        return statement.copy(
            expression = this.lowerExpressionModel(statement.expression),
            cases = statement.cases.map { lowerCase(it) }
        )
    }

    fun lowerForInStatementModel(statement: ForInStatementModel): ForInStatementModel {
        return statement.copy(
            variable = this.lowerVariableModel(NodeOwner(statement.variable, null), null),
            expression = this.lowerExpressionModel(statement.expression),
            body = lowerBlock(statement.body)
        )
    }

    override fun lower(statement: StatementModel): StatementModel {
        return when (statement) {
            is AssignmentStatementModel -> lowerAssignmentStatement(statement)
            is BlockStatementModel -> lowerBlock(statement)
            is IfStatementModel -> lowerIfStatement(statement)
            is ThrowStatementModel -> lowerThrowStatement(statement)
            is VariableModel -> this.lowerVariableModel(NodeOwner(statement, null), null)
            is WhileStatementModel -> lowerWhileStatement(statement)
            is ReturnStatementModel -> lowerReturnStatement(statement)
            is ExpressionStatementModel -> lowerExpression(statement)
            is WhenStatementModel -> lowerWhenStatementModel(statement)
            is ContinueStatementModel -> statement
            is BreakStatementModel -> statement
            is ForInStatementModel -> lowerForInStatementModel(statement)
            else -> statement
        }
    }

    fun lowerBlockStatement(block: BlockStatementModel): BlockStatementModel {
        return BlockStatementModel(block.statements.map { lower(it) })
    }
}
