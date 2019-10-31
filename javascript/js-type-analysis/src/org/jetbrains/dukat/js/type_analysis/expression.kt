package org.jetbrains.dukat.js.type_analysis

import org.jetbrains.dukat.js.interpretation.Scope
import org.jetbrains.dukat.js.type_analysis.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.RegExTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.StringTypeConstraint
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.RegExLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration

fun BinaryExpressionDeclaration.calculateConstraints(scope: Scope<ConstraintContainer>) : ConstraintContainer {
    val rightConstraints = right.calculateConstraints(scope)
    left.calculateConstraints(scope)

    return when (operator) {
        "=" -> {
            scope[left] = rightConstraints
            rightConstraints
        }
        "-", "*", "/", "**", "%", "++", "--", "-=", "*=", "/=", "%=", "**=" -> {
            scope[right]?.plusAssign(NumberTypeConstraint)
            scope[left]?.plusAssign(NumberTypeConstraint)
            ConstraintContainer(NumberTypeConstraint)
        }
        "&", "|", "^", "<<", ">>" -> {
            ConstraintContainer(NumberTypeConstraint)
        }
        "==", "===", "!=", "!==", ">", "<", ">=", "<=" -> {
            ConstraintContainer(BooleanTypeConstraint)
        }
        else -> {
            ConstraintContainer(NoTypeConstraint)
        }
    }
}

fun UnaryExpressionDeclaration.calculateConstraints(scope: Scope<ConstraintContainer>) : ConstraintContainer {
    return when (operator) {
        "--", "++", "~" -> {
            scope[operand]?.plusAssign(NumberTypeConstraint)
            ConstraintContainer(NumberTypeConstraint)
        }
        "-", "+" -> {
            ConstraintContainer(NumberTypeConstraint)
        }
        "!" -> {
            scope[operand]?.plusAssign(BooleanTypeConstraint)
            ConstraintContainer(BooleanTypeConstraint)
        }
        else -> {
            ConstraintContainer(NoTypeConstraint)
        }
    }
}

fun LiteralExpressionDeclaration.calculateConstraints() : ConstraintContainer {
    return when (this) {
        is StringLiteralExpressionDeclaration -> ConstraintContainer(StringTypeConstraint)
        is NumericLiteralExpressionDeclaration -> ConstraintContainer(NumberTypeConstraint)
        is BigIntLiteralExpressionDeclaration -> ConstraintContainer(BigIntTypeConstraint)
        is BooleanLiteralExpressionDeclaration -> ConstraintContainer(BooleanTypeConstraint)
        is RegExLiteralExpressionDeclaration -> ConstraintContainer(RegExTypeConstraint)
        else -> raiseConcern("Unexpected literal expression type <${this::class}>") { ConstraintContainer(NoTypeConstraint) }
    }
}

fun ExpressionDeclaration?.calculateConstraints(scope: Scope<ConstraintContainer>) : ConstraintContainer {
    return when (this) {
        is IdentifierExpressionDeclaration -> scope[this] ?: ConstraintContainer(NoTypeConstraint)
        is BinaryExpressionDeclaration -> this.calculateConstraints(scope)
        is UnaryExpressionDeclaration -> this.calculateConstraints(scope)
        is LiteralExpressionDeclaration -> this.calculateConstraints()
        null -> ConstraintContainer()
        else -> ConstraintContainer(NoTypeConstraint)
    }
}