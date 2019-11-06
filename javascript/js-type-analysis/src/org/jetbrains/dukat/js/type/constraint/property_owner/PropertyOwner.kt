package org.jetbrains.dukat.js.type.constraint.property_owner

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration

interface PropertyOwner {
    val propertyNames: Set<String>

    operator fun set(name: String, data: ConstraintContainer)

    operator fun set(identifier: IdentifierEntity, data: ConstraintContainer) {
        this[identifier.value] = data
    }

    operator fun set(identifierExpression: IdentifierExpressionDeclaration, data: ConstraintContainer) {
        this[identifierExpression.identifier] = data
    }

    //operator fun set(propertyAccessExpression: PropertyAccessExpressionDeclaration, data: ConstraintContainer)

    operator fun set(expression: ExpressionDeclaration, data: ConstraintContainer) {
        when (expression) {
            is IdentifierExpressionDeclaration -> this[expression] = data
            //is PropertyAccessExpressionDeclaration -> this[expression] = data
            else -> raiseConcern("Cannot set variable described by expression of type <${expression::class}>") {  }
        }
    }


    fun has(name: String) : Boolean

    fun has(identifier: IdentifierEntity) : Boolean {
        return has(identifier.value)
    }

    fun has(identifierExpression: IdentifierExpressionDeclaration) : Boolean {
        return has(identifierExpression.identifier)
    }

    //fun has(propertyAccessExpression: PropertyAccessExpressionDeclaration) : Boolean

    fun has(expression: ExpressionDeclaration) : Boolean {
        return when (expression) {
            is IdentifierExpressionDeclaration -> has(expression)
            //is PropertyAccessExpressionDeclaration -> has(expression)
            else -> raiseConcern("Cannot get variable described by expression of type <${expression::class}>") { false }
        }
    }


    operator fun get(name: String) : ConstraintContainer?

    operator fun get(identifier: IdentifierEntity) : ConstraintContainer? {
        return this[identifier.value]
    }

    operator fun get(identifierExpression: IdentifierExpressionDeclaration) : ConstraintContainer? {
        return this[identifierExpression.identifier]
    }

    //operator fun get(propertyAccessExpression: PropertyAccessExpressionDeclaration) : ConstraintContainer?

    operator fun get(expression: ExpressionDeclaration) : ConstraintContainer? {
        return when (expression) {
            is IdentifierExpressionDeclaration -> this[expression]
            //is PropertyAccessExpressionDeclaration -> this[expression]
            else -> raiseConcern("Cannot get variable described by expression of type <${expression::class}>") { null }
        }
    }
}