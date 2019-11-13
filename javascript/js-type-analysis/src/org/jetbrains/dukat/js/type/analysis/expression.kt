package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.reference.ReferenceConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.constraint.reference.call.CallArgumentConstraint
import org.jetbrains.dukat.js.type.constraint.reference.call.CallResultConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ObjectLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

fun BinaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    val rightConstraints = right.calculateConstraints(owner, path)
    val leftConstraints = left.calculateConstraints(owner, path)

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
            //TODO only calculate the constraints of the chosen path with this operator
            when (path.getNextDirection()) {
                PathWalker.Direction.First -> leftConstraints
                PathWalker.Direction.Second -> rightConstraints
            }
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

fun UnaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    val operandConstraints = operand.calculateConstraints(owner, path)

    return when (operator) {
        "--", "++", "~" -> {
            operandConstraints += NumberTypeConstraint
            owner[operand] = NumberTypeConstraint
            NumberTypeConstraint
        }
        "-", "+" -> {
            operandConstraints += NumberTypeConstraint
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

fun TypeOfExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    expression.calculateConstraints(owner, path)
    return StringTypeConstraint
}

fun CallExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    val callTargetConstraints = expression.calculateConstraints(owner, path)
    val argumentConstraints = arguments.map { it.calculateConstraints(owner, path) }

    argumentConstraints.forEachIndexed { argumentNumber, arg ->
        arg += CallArgumentConstraint(
                callTargetConstraints,
                argumentNumber
        )
    }

    return CallResultConstraint(
            callTargetConstraints
    )
}

fun ObjectLiteralExpressionDeclaration.calculateConstraints(path: PathWalker) : ObjectConstraint {
    val obj = ObjectConstraint()
    members.forEach { it.addTo(obj, path) }
    return obj
}

fun LiteralExpressionDeclaration.calculateConstraints(path: PathWalker) : Constraint {
    return when (this) {
        is StringLiteralExpressionDeclaration -> StringTypeConstraint
        is NumericLiteralExpressionDeclaration -> NumberTypeConstraint
        is BigIntLiteralExpressionDeclaration -> BigIntTypeConstraint
        is BooleanLiteralExpressionDeclaration -> BooleanTypeConstraint
        is ObjectLiteralExpressionDeclaration -> this.calculateConstraints(path)
        else -> raiseConcern("Unexpected literal expression type <${this::class}>") { CompositeConstraint(NoTypeConstraint) }
    }
}

fun ExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    return when (this) {
        is IdentifierExpressionDeclaration -> owner[this] ?: ReferenceConstraint(this.identifier)
        is PropertyAccessExpressionDeclaration -> owner[this] ?: CompositeConstraint() //TODO replace this with a reference constraint (of some sort)
        is BinaryExpressionDeclaration -> this.calculateConstraints(owner, path)
        is UnaryExpressionDeclaration -> this.calculateConstraints(owner, path)
        is TypeOfExpressionDeclaration -> this.calculateConstraints(owner, path)
        is CallExpressionDeclaration -> this.calculateConstraints(owner, path)
        is LiteralExpressionDeclaration -> this.calculateConstraints(path)
        else -> CompositeConstraint(NoTypeConstraint)
    }
}