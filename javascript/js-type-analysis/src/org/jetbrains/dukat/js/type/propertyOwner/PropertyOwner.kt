package org.jetbrains.dukat.js.type.propertyOwner

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.js.type.analysis.PathWalker
import org.jetbrains.dukat.js.type.analysis.calculateConstraints
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration

interface PropertyOwner {
    val owner: PropertyOwner?

    operator fun set(name: String, data: Constraint)

    operator fun set(identifier: IdentifierEntity, data: Constraint) {
        this[identifier.value] = data
    }

    operator fun set(identifierExpression: IdentifierExpressionDeclaration, data: Constraint) {
        this[identifierExpression.identifier] = data
    }

    operator fun set(
        propertyAccessExpression: PropertyAccessExpressionDeclaration,
        path: PathWalker,
        data: Constraint
    ) {
        val base = propertyAccessExpression.expression.calculateConstraints(this, path)

        if (base is PropertyOwner) {
            base[propertyAccessExpression.name] = data
        }
    }

    operator fun set(
        qualifierExpressionDeclaration: QualifierExpressionDeclaration,
        path: PathWalker,
        data: Constraint
    ) {
        val base = when (val leftSide = qualifierExpressionDeclaration.qualifier.left) {
            is QualifierEntity -> QualifierExpressionDeclaration(leftSide).calculateConstraints(this, path)
            is IdentifierEntity -> IdentifierExpressionDeclaration(leftSide).calculateConstraints(this, path)
        }

        if (base is PropertyOwner) {
            base[qualifierExpressionDeclaration.qualifier.right] = data
        }
    }

    operator fun set(expression: ExpressionDeclaration, path: PathWalker, data: Constraint) {
        when (expression) {
            is IdentifierExpressionDeclaration -> this[expression] = data
            is QualifierExpressionDeclaration -> this[expression, path] = data
            is PropertyAccessExpressionDeclaration -> this[expression, path] = data
            else -> raiseConcern("Cannot set variable described by expression of type <${expression::class}>") { }
        }
    }


    operator fun get(name: String): Constraint?

    operator fun get(identifier: IdentifierEntity): Constraint? {
        return this[identifier.value]
    }

    operator fun get(identifierExpression: IdentifierExpressionDeclaration): Constraint? {
        return this[identifierExpression.identifier]
    }

    operator fun get(propertyAccessExpression: PropertyAccessExpressionDeclaration, path: PathWalker): Constraint? {
        val base = propertyAccessExpression.expression.calculateConstraints(this, path)

        return if (base is PropertyOwner) {
            base[propertyAccessExpression.name]
        } else {
            null
        }
    }

    operator fun get(qualifierExpressionDeclaration: QualifierExpressionDeclaration, path: PathWalker): Constraint? {
        val base = when (val leftSide = qualifierExpressionDeclaration.qualifier.left) {
            is QualifierEntity -> QualifierExpressionDeclaration(leftSide).calculateConstraints(this, path)
            is IdentifierEntity -> IdentifierExpressionDeclaration(leftSide).calculateConstraints(this, path)
        }

        return if (base is PropertyOwner) {
            base[qualifierExpressionDeclaration.qualifier.right]
        } else {
            null
        }
    }

    operator fun get(expression: ExpressionDeclaration, path: PathWalker): Constraint? {
        return when (expression) {
            is IdentifierExpressionDeclaration -> this[expression]
            is QualifierExpressionDeclaration -> this[expression, path]
            is PropertyAccessExpressionDeclaration -> this[expression, path]
            else -> raiseConcern("Cannot get variable described by expression of type <${expression::class}>") { null }
        }
    }
}