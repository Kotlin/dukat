package org.jetbrains.dukat.js.type_analysis

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.NumberTypeConstraint
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration

fun constraintFromOperator(operator: String) : Set<Constraint> {
    return when (operator) {
        "-", "*", "/", "**", "%", "++", "--", "-=", "*=", "/=", "%=", "**=" -> setOf(NumberTypeConstraint)
        "==", "===", "!=", "!==", ">", "<", ">=", "<=", "&&", "!" -> setOf(BooleanTypeConstraint)
        else -> emptySet()
    }
}

fun BinaryExpressionDeclaration.calculateConstraints() : Set<Constraint> {
    return when(operator) {
        "=" -> right.calculateConstraints()
        else -> constraintFromOperator(operator)
    }
}

fun ExpressionDeclaration?.calculateConstraints() : Set<Constraint> {
    return when(this) {
        is BinaryExpressionDeclaration -> this.calculateConstraints()
        else -> emptySet()
    }
}