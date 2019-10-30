package org.jetbrains.dukat.js.type_analysis

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.RegExTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.StringTypeConstraint
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.RegExLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration

fun BinaryExpressionDeclaration.calculateConstraints() : Set<Constraint> {
    return when (operator) {
        "=" -> right.calculateConstraints()
        "-", "*", "/", "**", "%", "++", "--", "-=", "*=", "/=", "%=", "**=", //result and parameters must be numbers
        "&", "|", "^", "<<", ">>" //result must be a number
            -> setOf(NumberTypeConstraint)
        "==", "===", "!=", "!==", ">", "<", ">=", "<=" //result must be a boolean
            -> setOf(BooleanTypeConstraint)
        else -> setOf(NoTypeConstraint)
    }
}

fun LiteralExpressionDeclaration.calculateConstraints() : Set<Constraint> {
    return when (this) {
        is StringLiteralExpressionDeclaration -> setOf(StringTypeConstraint)
        is NumericLiteralExpressionDeclaration -> setOf(NumberTypeConstraint)
        is BigIntLiteralExpressionDeclaration -> setOf(BigIntTypeConstraint)
        is RegExLiteralExpressionDeclaration -> setOf(RegExTypeConstraint)
        else -> raiseConcern("Unexpected literal expression type <${this.javaClass}>") { setOf(NoTypeConstraint) }
    }
}

fun ExpressionDeclaration?.calculateConstraints() : Set<Constraint> {
    return when (this) {
        is BinaryExpressionDeclaration -> this.calculateConstraints()
        is LiteralExpressionDeclaration -> this.calculateConstraints()
        null -> emptySet()
        else -> setOf(NoTypeConstraint)
    }
}