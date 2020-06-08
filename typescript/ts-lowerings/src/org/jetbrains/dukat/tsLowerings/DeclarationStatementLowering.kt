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

open class DeclarationStatementLowering : ExpressionLowering {

    open fun lowerBlock(statement: BlockDeclaration): BlockDeclaration {
        return statement.copy(
                statements = statement.statements.map { lower(it) }
        )
    }

    open fun lowerIfStatement(statement: IfStatementDeclaration): IfStatementDeclaration {
        return statement.copy(
                condition = this.lowerExpressionDeclaration(statement.condition),
                thenStatement = this.lowerBlock(statement.thenStatement),
                elseStatement = statement.elseStatement?.let { this.lowerBlock(it) }
        )
    }

    open fun lowerThrowStatement(statement: ThrowStatementDeclaration): ThrowStatementDeclaration {
        return statement.copy(expression = this.lowerExpressionDeclaration(statement.expression))
    }

    open fun lowerWhileStatement(statement: WhileStatementDeclaration): WhileStatementDeclaration {
        return statement.copy(
                condition = this.lowerExpressionDeclaration(statement.condition),
                statement = this.lowerBlock(statement.statement)
        )
    }

    open fun lowerReturnStatement(statement: ReturnStatementDeclaration): ReturnStatementDeclaration {
        return statement.copy(
                expression = statement.expression?.let { this.lowerExpressionDeclaration(it) }
        )
    }

    private fun lowerExpressionDeclaration(expression: ExpressionDeclaration): ExpressionDeclaration {
        return this.lower(expression)
    }

    open fun lowerExpression(statement: ExpressionStatementDeclaration): ExpressionStatementDeclaration {
        return statement.copy(
                expression = this.lowerExpressionDeclaration(statement.expression)
        )
    }

    open fun lowerForStatementDeclaration(statement: ForStatementDeclaration): StatementDeclaration {
        return statement.copy(
                initializer = this.lowerBlock(statement.initializer),
                condition = statement.condition?.let { this.lowerExpressionDeclaration(it) },
                incrementor = statement.incrementor?.let { this.lowerExpressionDeclaration(it) },
                body = lowerBlock(statement.body)
        )
    }

    open fun lowerCase(statement: CaseDeclaration): CaseDeclaration {
        return statement.copy(
                condition = statement.condition?.let { this.lowerExpressionDeclaration(it) },
                body = lowerBlock(statement.body)
        )
    }

    open fun lowerSwitchStatementDeclaration(statement: SwitchStatementDeclaration): SwitchStatementDeclaration {
        return statement.copy(
                expression = this.lowerExpressionDeclaration(statement.expression),
                cases = statement.cases.map { lowerCase(it) }
        )
    }

    open fun lowerForOfStatementDeclaration(statement: ForOfStatementDeclaration): ForOfStatementDeclaration {
        return statement.copy(
            variable = this.lowerVariableLikeDeclaration(statement.variable),
            expression = this.lowerExpressionDeclaration(statement.expression),
            body = lowerBlock(statement.body)
        )
    }

    open fun lowerVariableLikeDeclaration(statement: VariableLikeDeclaration): VariableLikeDeclaration {
        return when (statement) {
            is VariableDeclaration -> lowerVariableDeclaration(statement)
            is ArrayDestructuringDeclaration -> lowerArrayDestructuringDeclaration(statement)
            else -> statement
        }
    }

    open fun lowerBindingVariableDeclaration(statement: BindingVariableDeclaration): BindingVariableDeclaration {
        return statement.copy(initializer = statement.initializer?.let { lowerExpressionDeclaration(it) })
    }

    open fun lowerVariableDeclaration(statement: VariableDeclaration): VariableDeclaration {
        return statement.copy(initializer = statement.initializer?.let { lowerExpressionDeclaration(it) })
    }

    open fun lowerArrayDestructuringDeclaration(statement: ArrayDestructuringDeclaration): ArrayDestructuringDeclaration {
        return statement.copy(elements = statement.elements.map { lowerBindingElementDeclaration(it) })
    }

    open fun lowerBindingElementDeclaration(statement: BindingElementDeclaration): BindingElementDeclaration {
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

    private fun lowerBlockStatement(block: BlockDeclaration): BlockDeclaration {
        return BlockDeclaration(block.statements.map { lower(it) })
    }

    private fun lowerTopLevelVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return declaration.copy(
            initializer = declaration.initializer?.let { lower(it) }
        )
    }

    private fun lowerConstructorDeclaration(declaration: ConstructorDeclaration): ConstructorDeclaration {
        return declaration.copy(body = declaration.body?.let { lowerBlockStatement(it) })
    }

    private fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(body = declaration.body?.let { lowerBlockStatement(it) })
    }

    private fun lowerPropertyDeclaration(declaration: PropertyDeclaration): PropertyDeclaration {
        return declaration.copy(initializer = declaration.initializer?.let { lower(it) })
    }

    private fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration {
        return when (declaration) {
            is ConstructorDeclaration -> lowerConstructorDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is PropertyDeclaration -> lowerPropertyDeclaration(declaration)
            else -> declaration
        }
    }

    private fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        val newMembers = declaration.members.map { lowerMemberDeclaration(it) }
        return when (declaration) {
            is InterfaceDeclaration -> declaration.copy(members = newMembers)
            is ClassDeclaration -> declaration.copy(members = newMembers)
            is GeneratedInterfaceDeclaration -> declaration.copy(members = newMembers)
            else -> declaration
        }
    }

    private fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is VariableDeclaration -> lowerTopLevelVariableDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration)
            is ModuleDeclaration -> lower(declaration)
            else -> declaration
        }
    }

    private fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lower(documentRoot: ModuleDeclaration): ModuleDeclaration {
        return documentRoot.copy(declarations = lowerTopLevelDeclarations(documentRoot.declarations))
    }

}