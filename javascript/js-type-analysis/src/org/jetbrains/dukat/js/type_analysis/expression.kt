package org.jetbrains.dukat.js.type_analysis

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.NumberTypeConstraint
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration

fun BinaryExpressionDeclaration.calculateConstraints() : Set<Constraint> {
    return when (operator) {
        "=" -> right.calculateConstraints()
        "-", "*", "/", "**", "%", "++", "--", "-=", "*=", "/=", "%=", "**=", //result and parameters must be numbers
        "&", "|", "^", "<<", ">>" //result must be a number
            -> setOf(NumberTypeConstraint)
        "==", "===", "!=", "!==", ">", "<", ">=", "<=" //result must be a boolean
            -> setOf(BooleanTypeConstraint)
        else -> emptySet()
    }
}

fun ExpressionDeclaration?.calculateConstraints() : Set<Constraint> {
    return when(this) {
        is BinaryExpressionDeclaration -> this.calculateConstraints()
        else -> emptySet()
    }
}