package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.reference.ReferenceConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.CallableConstraint
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.VoidTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ClassConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.reference.call.CallArgumentConstraint
import org.jetbrains.dukat.js.type.constraint.reference.call.CallResultConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.constraint.properties.PropertyOwnerConstraint
import org.jetbrains.dukat.js.type.propertyOwner.Scope
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ConditionalExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NewExpressionDeclaration
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

fun FunctionDeclaration.addTo(owner: PropertyOwner) : FunctionConstraint? {
    return this.body?.let {
        val pathWalker = PathWalker()

        val versions = mutableListOf<FunctionConstraint.Overload>()

        do {
            val functionScope = Scope(owner)

            val parameterConstraints = MutableList(parameters.size) { i ->
                // Store constraints of parameters in scope,
                // and in parameter list (in case the variable is replaced)
                val parameterConstraint = CompositeConstraint(owner)
                functionScope[parameters[i].name] = parameterConstraint
                parameters[i].name to parameterConstraint
            }

            val returnTypeConstraints = it.calculateConstraints(functionScope, pathWalker) ?: VoidTypeConstraint

            versions.add(FunctionConstraint.Overload(
                    returnConstraints = returnTypeConstraints,
                    parameterConstraints = parameterConstraints
            ))
        } while (pathWalker.startNextPath())

        val functionConstraint = FunctionConstraint(owner, versions)

        if (name != "") {
            owner[name] = functionConstraint
        }

        functionConstraint
    }
}

fun ClassDeclaration.addTo(owner: PropertyOwner, path: PathWalker) : ClassConstraint? {
    val className = name // Needed for smart cast
    return if(className is IdentifierEntity) {
        val classConstraint = ClassConstraint(owner)

        members.forEach { it.addToClass(classConstraint, path) }

        if (className.value != "") {
            owner[className.value] = classConstraint
        }

        classConstraint
    } else {
        raiseConcern("Cannot convert class with name of type <${className::class}>.") { null }
    }
}


fun BinaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    return when (operator) {
        // Assignments
        "=" -> {
            val rightConstraints = right.calculateConstraints(owner, path)
            owner[left, path] = rightConstraints
            rightConstraints
        }
        "-=", "*=", "/=", "%=", "**=", "&=", "^=", "|=", "<<=", ">>=", ">>>=" -> {
            right.calculateConstraints(owner, path)
            owner[left, path] = NumberTypeConstraint
            NumberTypeConstraint
        }

        // Non-assignments
        "&&", "||" -> {
            when (path.getNextDirection()) {
                PathWalker.Direction.Left -> {
                    left.calculateConstraints(owner, path)
                    //right isn't being evaluated
                }
                PathWalker.Direction.Right -> {
                    left.calculateConstraints(owner, path)
                    right.calculateConstraints(owner, path)
                }
            }
        }
        "-", "*", "/", "**", "%", "++", "--" -> {
            left.calculateConstraints(owner, path) += NumberTypeConstraint
            right.calculateConstraints(owner, path) += NumberTypeConstraint
            NumberTypeConstraint
        }
        "&", "|", "^", "<<", ">>", ">>>" -> {
            left.calculateConstraints(owner, path)
            right.calculateConstraints(owner, path)
            NumberTypeConstraint
        }
        "==", "===", "!=", "!==", "in", "instanceof" -> {
            left.calculateConstraints(owner, path)
            right.calculateConstraints(owner, path)
            BooleanTypeConstraint
        }
        ">", "<", ">=", "<=" -> {
            left.calculateConstraints(owner, path) += NumberTypeConstraint
            right.calculateConstraints(owner, path) += NumberTypeConstraint
            BooleanTypeConstraint
        }
        else -> {
            left.calculateConstraints(owner, path)
            right.calculateConstraints(owner, path)
            CompositeConstraint(owner, NoTypeConstraint)
        }
    }
}

fun UnaryExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    val operandConstraints = operand.calculateConstraints(owner, path)

    return when (operator) {
        "--", "++", "~" -> {
            operandConstraints += NumberTypeConstraint
            owner[operand, path] = NumberTypeConstraint
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
            CompositeConstraint(owner, NoTypeConstraint)
        }
    }
}

fun TypeOfExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    expression.calculateConstraints(owner, path)
    return StringTypeConstraint
}

fun CallExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    val callTargetConstraints = expression.calculateConstraints(owner, path)
    var callResultConstraint: Constraint = CallResultConstraint(owner, callTargetConstraints)
    val argumentConstraints = arguments.map { it.calculateConstraints(owner, path) }

    argumentConstraints.forEachIndexed { argumentNumber, arg ->
        arg += CallArgumentConstraint(
                owner,
                callTargetConstraints,
                argumentNumber
        )
    }

    if (callTargetConstraints is CompositeConstraint) {
        callResultConstraint = CompositeConstraint(owner)

        //TODO add arguments to CallableConstraint
        callTargetConstraints += CallableConstraint(argumentConstraints.size, callResultConstraint)
    }

    return callResultConstraint
}

fun NewExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    val classConstraints = expression.calculateConstraints(owner, path) as PropertyOwnerConstraint

    //TODO add constraints for constructor call
    arguments.map { it.calculateConstraints(owner, path) }

    return ObjectConstraint(
            owner = owner,
            instantiatedClass = classConstraints
    )
}

fun ConditionalExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    condition.calculateConstraints(owner, path)

    return when (path.getNextDirection()) {
        PathWalker.Direction.Left -> whenTrue.calculateConstraints(owner, path)
        PathWalker.Direction.Right -> whenFalse.calculateConstraints(owner, path)
    }
}

fun ObjectLiteralExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : ObjectConstraint {
    val obj = ObjectConstraint(owner)
    members.forEach { it.addTo(obj, path) }
    return obj
}

fun LiteralExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    return when (this) {
        is StringLiteralExpressionDeclaration -> StringTypeConstraint
        is NumericLiteralExpressionDeclaration -> NumberTypeConstraint
        is BigIntLiteralExpressionDeclaration -> BigIntTypeConstraint
        is BooleanLiteralExpressionDeclaration -> BooleanTypeConstraint
        is ObjectLiteralExpressionDeclaration -> this.calculateConstraints(owner, path)
        else -> raiseConcern("Unexpected literal expression type <${this::class}>") { CompositeConstraint(owner, NoTypeConstraint) }
    }
}

fun ExpressionDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    return when (this) {
        is FunctionDeclaration -> this.addTo(owner) ?: NoTypeConstraint
        is ClassDeclaration -> this.addTo(owner, path) ?: NoTypeConstraint
        is IdentifierExpressionDeclaration -> owner[this] ?: ReferenceConstraint(this.identifier, owner)
        is PropertyAccessExpressionDeclaration -> owner[this, path] ?: CompositeConstraint(owner)
        is BinaryExpressionDeclaration -> this.calculateConstraints(owner, path)
        is UnaryExpressionDeclaration -> this.calculateConstraints(owner, path)
        is TypeOfExpressionDeclaration -> this.calculateConstraints(owner, path)
        is CallExpressionDeclaration -> this.calculateConstraints(owner, path)
        is NewExpressionDeclaration -> this.calculateConstraints(owner, path)
        is ConditionalExpressionDeclaration -> this.calculateConstraints(owner, path)
        is LiteralExpressionDeclaration -> this.calculateConstraints(owner, path)
        else -> CompositeConstraint(owner, NoTypeConstraint)
    }
}