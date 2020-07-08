package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private data class Scope(
    val variables: MutableMap<String, ParameterValueDeclaration> = mutableMapOf()
)

internal class StatementTypeContext {

    private val scopes = mutableListOf<Scope>()

    private fun ParameterValueDeclaration.isBoolean() = this == TypeDeclaration(
        IdentifierEntity("Boolean"),
        listOf()
    )

    // if we are not sure, returns true
    fun hasBooleanType(expression: ExpressionDeclaration): Boolean {
        when (expression) {
            is IdentifierExpressionDeclaration -> {
                val registeredType = scopes.mapNotNull { it.variables[expression.identifier.value] }.lastOrNull()
                return registeredType?.let {
                    it.isBoolean()
                } ?: true
            }
        }
        return true
    }

    fun registerVariable(variable: VariableDeclaration) {
        scopes.lastOrNull()?.let {
            it.variables[variable.name] = variable.type
        } ?: raiseConcern("no open scope to register variable $variable") { }
    }

    fun registerParameter(parameter: ParameterDeclaration) {
        scopes.lastOrNull()?.let {
            it.variables[parameter.name] = parameter.type
        } ?: raiseConcern("no open scope to register parameter $parameter") { }
    }

    fun startScope() {
        scopes += Scope()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun endScope() {
        scopes.removeLast()
    }
}