package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.composite.ReferenceConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.call.CallArgumentConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.call.CallResultConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.VoidTypeConstraint
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

fun BinaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner) : Constraint {
    val rightConstraints = right.calculateConstraints(owner)
    val leftConstraints = left.calculateConstraints(owner)

    return when (operator) {
        // Assignments
        "=" -> {
            owner[left] = rightConstraints
            rightConstraints
        }
        "-=", "*=", "/=", "%=", "**=", "&=", "^=", "|=", "<<=", ">>=", ">>>=" -> {
            owner[left] = NumberTypeConstraint
            NumberTypeConstraint
        }

        // Non-assignments
        "&&", "||" -> {
            //TODO make this branching
            leftConstraints
        }
        "-", "*", "/", "**", "%", "++", "--" -> {
            rightConstraints += NumberTypeConstraint
            leftConstraints += NumberTypeConstraint
            NumberTypeConstraint
        }
        "&", "|", "^", "<<", ">>", ">>>" -> {
            NumberTypeConstraint
        }
        "==", "===", "!=", "!==", ">", "<", ">=", "<=", "in", "instanceof" -> {
            BooleanTypeConstraint
        }
        else -> {
            CompositeConstraint(NoTypeConstraint)
        }
    }
}

fun UnaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner) : Constraint {
    val operandConstraints = operand.calculateConstraints(owner)

    return when (operator) {
        "--", "++", "~" -> {
            operandConstraints += NumberTypeConstraint
            NumberTypeConstraint
        }
        "-", "+" -> {
            NumberTypeConstraint
        }
        "!" -> {
            BooleanTypeConstraint
        }
        else -> {
            CompositeConstraint(NoTypeConstraint)
        }
    }
}

fun TypeOfExpressionDeclaration.calculateConstraints(owner: PropertyOwner) : Constraint {
    expression.calculateConstraints(owner)
    return StringTypeConstraint
}

fun CallExpressionDeclaration.calculateConstraints(owner: PropertyOwner) : Constraint {
    val callTargetConstraints = expression.calculateConstraints(owner)
    val argumentConstraints = arguments.map { it.calculateConstraints(owner) }

    argumentConstraints.forEachIndexed { argumentNumber, arg ->
        arg += CallArgumentConstraint(
                callTargetConstraints,
                argumentConstraints,
                argumentNumber
        )
    }

    return CallResultConstraint(
            callTargetConstraints,
            argumentConstraints
    )
}

fun LiteralExpressionDeclaration.calculateConstraints() : Constraint {
    return when (this) {
        is StringLiteralExpressionDeclaration -> StringTypeConstraint
        is NumericLiteralExpressionDeclaration -> NumberTypeConstraint
        is BigIntLiteralExpressionDeclaration -> BigIntTypeConstraint
        is BooleanLiteralExpressionDeclaration -> BooleanTypeConstraint
        else -> raiseConcern("Unexpected literal expression type <${this::class}>") { CompositeConstraint(NoTypeConstraint) }
    }
}

fun ExpressionDeclaration?.calculateConstraints(owner: PropertyOwner) : Constraint {
    return when (this) {
        is IdentifierExpressionDeclaration -> owner[this] ?: ReferenceConstraint(this.identifier)
        is BinaryExpressionDeclaration -> this.calculateConstraints(owner)
        is UnaryExpressionDeclaration -> this.calculateConstraints(owner)
        is TypeOfExpressionDeclaration -> this.calculateConstraints(owner)
        is CallExpressionDeclaration -> this.calculateConstraints(owner)
        is LiteralExpressionDeclaration -> this.calculateConstraints()
        null -> VoidTypeConstraint
        else -> CompositeConstraint()
    }
}