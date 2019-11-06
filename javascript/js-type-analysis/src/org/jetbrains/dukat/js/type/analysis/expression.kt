package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.constraint.unresolved.ReferenceConstraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.resolved.StringTypeConstraint
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

fun BinaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner) : ConstraintContainer {
    val rightConstraints = right.calculateConstraints(owner)
    val leftConstraints = left.calculateConstraints(owner)

    return when (operator) {
        "=" -> {
            owner[left] = rightConstraints
            rightConstraints
        }
        "&&", "||" -> {
            //TODO make this branching
            leftConstraints
        }
        "-", "*", "/", "**", "%", "++", "--", "-=", "*=", "/=", "%=", "**=" -> {
            rightConstraints += NumberTypeConstraint
            leftConstraints += NumberTypeConstraint
            ConstraintContainer(NumberTypeConstraint)
        }
        "&", "|", "^", "<<", ">>" -> {
            ConstraintContainer(NumberTypeConstraint)
        }
        "==", "===", "!=", "!==", ">", "<", ">=", "<=", "in" -> {
            ConstraintContainer(BooleanTypeConstraint)
        }
        else -> {
            ConstraintContainer(NoTypeConstraint)
        }
    }
}

fun UnaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner) : ConstraintContainer {
    val operandConstraints = operand.calculateConstraints(owner)

    return when (operator) {
        "--", "++", "~" -> {
            operandConstraints += NumberTypeConstraint
            ConstraintContainer(NumberTypeConstraint)
        }
        "-", "+" -> {
            ConstraintContainer(NumberTypeConstraint)
        }
        "!" -> {
            ConstraintContainer(BooleanTypeConstraint)
        }
        else -> {
            ConstraintContainer(NoTypeConstraint)
        }
    }
}

fun TypeOfExpressionDeclaration.calculateConstraints(owner: PropertyOwner) : ConstraintContainer {
    expression.calculateConstraints(owner)
    return ConstraintContainer(StringTypeConstraint)
}

fun LiteralExpressionDeclaration.calculateConstraints() : ConstraintContainer {
    return when (this) {
        is StringLiteralExpressionDeclaration -> ConstraintContainer(StringTypeConstraint)
        is NumericLiteralExpressionDeclaration -> ConstraintContainer(NumberTypeConstraint)
        is BigIntLiteralExpressionDeclaration -> ConstraintContainer(BigIntTypeConstraint)
        is BooleanLiteralExpressionDeclaration -> ConstraintContainer(BooleanTypeConstraint)
        else -> raiseConcern("Unexpected literal expression type <${this::class}>") { ConstraintContainer(NoTypeConstraint) }
    }
}

fun ExpressionDeclaration?.calculateConstraints(owner: PropertyOwner) : ConstraintContainer {
    return when (this) {
        is IdentifierExpressionDeclaration -> owner[this] ?: ConstraintContainer(ReferenceConstraint(this.identifier))
        is BinaryExpressionDeclaration -> this.calculateConstraints(owner)
        is UnaryExpressionDeclaration -> this.calculateConstraints(owner)
        is TypeOfExpressionDeclaration -> this.calculateConstraints(owner)
        is LiteralExpressionDeclaration -> this.calculateConstraints()
        null -> ConstraintContainer()
        else -> ConstraintContainer(NoTypeConstraint)
    }
}