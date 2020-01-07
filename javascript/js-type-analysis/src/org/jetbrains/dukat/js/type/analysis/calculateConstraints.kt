package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.ThrowConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.VoidTypeConstraint
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.ThrowStatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration

fun BlockDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    return statements.calculateConstraints(owner, path)
}

fun VariableDeclaration.addTo(owner: PropertyOwner, path: PathWalker) {
    owner[name] = initializer?.calculateConstraints(owner, path) ?: NoTypeConstraint
}

fun IfStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    condition.calculateConstraints(owner, path)

    return when (path.getNextDirection()) {
        PathWalker.Direction.Left -> thenStatement.calculateConstraints(owner, path)
        PathWalker.Direction.Right -> elseStatement?.calculateConstraints(owner, path)
    }
}

fun WhileStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    condition.calculateConstraints(owner, path)

    return when (path.getNextDirection()) {
        PathWalker.Direction.Left -> statement.calculateConstraints(owner, path)
        PathWalker.Direction.Right -> null
    }
}

fun ExpressionStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) {
    expression.calculateConstraints(owner, path)
}

fun ReturnStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    return expression?.calculateConstraints(owner, path) ?: VoidTypeConstraint
}

fun ThrowStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    expression?.calculateConstraints(owner, path)
    return ThrowConstraint
}

fun TopLevelDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    when (this) {
        is FunctionDeclaration -> this.addTo(owner)
        is ClassDeclaration -> this.addTo(owner, path)
        is VariableDeclaration -> this.addTo(owner, path)
        is IfStatementDeclaration -> return this.calculateConstraints(owner, path)
        is WhileStatementDeclaration -> return this.calculateConstraints(owner, path)
        is ExpressionStatementDeclaration -> this.calculateConstraints(owner, path)
        is BlockDeclaration -> return this.calculateConstraints(owner, path)
        is ReturnStatementDeclaration -> return this.calculateConstraints(owner, path)
        is ThrowStatementDeclaration -> return this.calculateConstraints(owner, path)
        else -> raiseConcern("Unexpected top level entity type <${this::class}>") {  }
    }

    return null
}

fun List<TopLevelDeclaration>.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    this.forEach { statement ->
        val statementReturnTypeConstraints = statement.calculateConstraints(owner, path)

        if(statementReturnTypeConstraints != null) {
            return statementReturnTypeConstraints
        }
    }

    return null
}

fun ModuleDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) = declarations.calculateConstraints(owner, path)
