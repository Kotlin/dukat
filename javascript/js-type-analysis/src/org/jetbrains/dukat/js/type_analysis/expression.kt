package org.jetbrains.dukat.js.type_analysis

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

fun constraintFromOperator(operator: String) : TypeDeclaration {
    return when (operator) {
        "-", "*", "/", "**", "%", "++", "--", "-=", "*=", "/=", "%=", "**=" -> TypeDeclaration(
                value = IdentifierEntity("number"),
                params = emptyList(),
                nullable = false
        )
        "==", "===", "!=", "!==", ">", "<", ">=", "<=", "&&", "||", "!" -> TypeDeclaration(
                value = IdentifierEntity("boolean"),
                params = emptyList(),
                nullable = false
        )
        else -> TypeDeclaration(
                value = IdentifierEntity("Any"),
                params = emptyList(),
                nullable = true
        )
    }
}

fun BinaryExpressionDeclaration.calculateConstraint() : TypeDeclaration {
    return when(operator) {
        "=" -> right.calculateConstraint()
        else -> constraintFromOperator(operator)
    }
}

fun ExpressionDeclaration?.calculateConstraint() : TypeDeclaration {
    return when(this) {
        is BinaryExpressionDeclaration -> this.calculateConstraint()
        null -> TypeDeclaration(
                value = IdentifierEntity("Unit"),
                params = emptyList(),
                nullable = false
        )
        else -> TypeDeclaration(
                value = IdentifierEntity("Any"),
                params = emptyList(),
                nullable = true
        )
    }
}