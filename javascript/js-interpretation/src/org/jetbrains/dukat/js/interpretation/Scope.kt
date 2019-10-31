package org.jetbrains.dukat.js.interpretation

import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration


/**
 * Models variable behaviour in javascript execution.
 * This includes variable access in nested scopes.
 *
 * @param T Type of data stored for the variables.
 * @property parent Parent scope of this scope.
 */
class Scope<T>(
        private val parent: Scope<T>? = null
) {
    private val scopeData = mutableMapOf<String, T?>()

    operator fun set(name: String, data: T?) {
        scopeData[name] = data
    }

    operator fun set(identifierExpression: IdentifierExpressionDeclaration, data: T?) {
        this[identifierExpression.identifier.value] = data
    }

    operator fun set(expression: ExpressionDeclaration, data: T?) {
        return when (expression) {
            is IdentifierExpressionDeclaration -> this[expression] = data
            else -> raiseConcern("Cannot set variable described by expression of type <${expression::class}>") {  }
        }
    }


    fun assign(name: String, data: T?) {
        if (scopeData.contains(name) || parent == null) {
            scopeData[name] = data
        } else {
            parent.assign(name, data)
        }
    }

    fun assign(identifierExpression: IdentifierExpressionDeclaration, data: T?) {
        this.assign(identifierExpression.identifier.value, data)
    }


    fun getProperty(name: String) : T? {
        return scopeData[name] ?: raiseConcern("Trying to access un-set property <$name>") { null }
    }

    fun getProperty(identifierExpression: IdentifierExpressionDeclaration) : T? {
        return this.getProperty(identifierExpression.identifier.value)
    }

    fun getProperty(identifier: LiteralExpressionDeclaration) : T? {
        return when (identifier) {
            is StringLiteralExpressionDeclaration -> this.getProperty(identifier.value)
            is NumericLiteralExpressionDeclaration -> this.getProperty(identifier.value)
            is BigIntLiteralExpressionDeclaration -> this.getProperty(identifier.value)
            is BooleanLiteralExpressionDeclaration -> this.getProperty(identifier.value.toString())
            else -> raiseConcern("Cannot access property of scope using literal identifier of type <${identifier::class}>") { null }
        }
    }


    operator fun get(name: String) : T? {
        return scopeData[name] ?: parent?.get(name) ?: raiseConcern("Trying to get un-set variable '$name'") { null }
    }

    operator fun get(identifierExpression: IdentifierExpressionDeclaration) : T? {
        return this[identifierExpression.identifier.value]
    }

    operator fun get(expression: ExpressionDeclaration): T? {
        return when (expression) {
            is IdentifierExpressionDeclaration -> this[expression]
            else -> raiseConcern("Cannot get variable described by expression of type <${expression::class}>") { null }
        }
    }

}
