package org.jetbrains.dukat.js.type.property_owner

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

interface PropertyOwner {
    val propertyNames: Set<String>

    operator fun set(name: String, data: Constraint)

    operator fun set(identifier: IdentifierEntity, data: Constraint) {
        this[identifier.value] = data
    }

    operator fun set(identifierExpression: IdentifierExpressionDeclaration, data: Constraint) {
        this[identifierExpression.identifier] = data
    }

    operator fun set(propertyAccessExpression: PropertyAccessExpressionDeclaration, data: Constraint) {
        val base = this[propertyAccessExpression.expression]

        if (base is PropertyOwner) {
            base[propertyAccessExpression.name] = data
        }
    }

    operator fun set(expression: ExpressionDeclaration, data: Constraint) {
        when (expression) {
            is IdentifierExpressionDeclaration -> this[expression] = data
            is PropertyAccessExpressionDeclaration -> this[expression] = data
            else -> raiseConcern("Cannot set variable described by expression of type <${expression::class}>") {  }
        }
    }

    /*
    fun has(name: String) : Boolean

    fun has(identifier: IdentifierEntity) : Boolean {
        return has(identifier.value)
    }

    fun has(identifierExpression: IdentifierExpressionDeclaration) : Boolean {
        return has(identifierExpression.identifier)
    }

    fun has(propertyAccessExpression: PropertyAccessExpressionDeclaration) : Boolean

    fun has(expression: ExpressionDeclaration) : Boolean {
        return when (expression) {
            is IdentifierExpressionDeclaration -> has(expression)
            is PropertyAccessExpressionDeclaration -> has(expression)
            else -> raiseConcern("Cannot get variable described by expression of type <${expression::class}>") { false }
        }
    }
    */

    operator fun get(name: String) : Constraint?

    operator fun get(identifier: IdentifierEntity) : Constraint? {
        return this[identifier.value]
    }

    operator fun get(identifierExpression: IdentifierExpressionDeclaration) : Constraint? {
        return this[identifierExpression.identifier]
    }

    operator fun get(propertyAccessExpression: PropertyAccessExpressionDeclaration) : Constraint? {
        val base = this[propertyAccessExpression.expression]

        return if (base is PropertyOwner) {
            base[propertyAccessExpression.name]
        } else {
            null
        }
    }

    operator fun get(expression: ExpressionDeclaration) : Constraint? {
        return when (expression) {
            is IdentifierExpressionDeclaration -> this[expression]
            is PropertyAccessExpressionDeclaration -> this[expression]
            else -> raiseConcern("Cannot get variable described by expression of type <${expression::class}>") { null }
        }
    }
}