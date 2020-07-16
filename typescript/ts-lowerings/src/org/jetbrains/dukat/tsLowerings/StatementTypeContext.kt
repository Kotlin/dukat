package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private data class Scope(
    val variables: MutableMap<NameEntity, ParameterValueDeclaration> = mutableMapOf(),
    val expressionsToReplace: MutableSet<ExpressionDeclaration> = mutableSetOf()
)

internal class StatementTypeContext : DeclarationLowering {

    private val scopes = mutableListOf<Scope>()

    // TODO: use uids as keys instead of short names
    private val properties = mutableMapOf<String, MutableList<PropertyDeclaration>>()

    private fun ParameterValueDeclaration.isBoolean() = this == TypeDeclaration(
        IdentifierEntity("Boolean"),
        listOf()
    )

    // if we are not sure, returns true
    fun hasBooleanType(expression: ExpressionDeclaration): Boolean {
        when (expression) {
            is IdentifierExpressionDeclaration -> {
                val registeredType = scopes.mapNotNull {
                    it.variables[IdentifierEntity(expression.identifier.value)]
                }.lastOrNull()
                return registeredType?.let {
                    it.isBoolean()
                } ?: true
            }
            is QualifierExpressionDeclaration -> {
                val registeredType = scopes.mapNotNull { it.variables[expression.qualifier.left] }.lastOrNull()
                return registeredType?.let {
                    findPropertyInType(it, expression.qualifier.right.value)?.let { property ->
                        property.type.isBoolean()
                    }
                } ?: true
            }
        }
        return true
    }

    fun registerVariable(variable: VariableDeclaration) {
        scopes.lastOrNull()?.let {
            it.variables[IdentifierEntity(variable.name)] = variable.type
        } ?: raiseConcern("no open scope to register variable $variable") { }
    }

    fun registerParameter(parameter: ParameterDeclaration) {
        scopes.lastOrNull()?.let {
            it.variables[IdentifierEntity(parameter.name)] = parameter.type
        } ?: raiseConcern("no open scope to register parameter $parameter") { }
    }

    fun startScope() {
        scopes += Scope()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun endScope() {
        scopes.removeLast()
    }

    fun addExpressionToReplace(expression: ExpressionDeclaration) {
        scopes.last().expressionsToReplace += expression
    }

    fun getRelevantExpressionsToReplace(): Set<ExpressionDeclaration> {
        val expressions = scopes.last().expressionsToReplace.toSet()
        scopes.last().expressionsToReplace.clear()
        return expressions
    }

    override fun lowerClassLikeDeclaration(
        declaration: ClassLikeDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): TopLevelDeclaration? {
        properties.getOrPut((declaration.name as IdentifierEntity).value) {
            mutableListOf()
        } += declaration.members.filterIsInstance<PropertyDeclaration>()
        return super.lowerClassLikeDeclaration(declaration, owner)
    }

    private fun findPropertyInType(type: ParameterValueDeclaration, propertyName: String): PropertyDeclaration? {
        if (type !is TypeDeclaration) {
            return null
        }
        return properties[type.value.rightMost().value]?.let {
            it.find { property ->
                property.name == propertyName
            }
        }
    }
}