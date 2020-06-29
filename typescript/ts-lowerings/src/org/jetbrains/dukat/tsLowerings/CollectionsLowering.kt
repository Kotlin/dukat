package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NewExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.SpreadExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration

private class CollectionsLowering : DeclarationLowering {

    private val setMethodRenames = mapOf(
        "has" to "contains",
        "delete" to "remove"
    )

    private val mapMethodRenames = mapOf(
        "has" to "contains"
    )

    private val constructorRenames = mapOf(
        "Set" to "mutableSetOf",
        "Array" to "arrayOf",
        "Map" to "mutableMapOf"
    )

    private fun lowerNewExpressionDeclaration(declaration: NewExpressionDeclaration): ExpressionDeclaration {
        val expression = declaration.expression
        if (expression is IdentifierExpressionDeclaration) {
            for ((old, new) in constructorRenames) {
                if (expression.identifier.value == old) {
                    return CallExpressionDeclaration(
                        expression = IdentifierExpressionDeclaration(
                            IdentifierEntity(new)
                        ),
                        arguments = declaration.arguments,
                        typeArguments = declaration.typeArguments
                    )
                }
            }
        }
        return declaration
    }

    private fun transformPush(variableExpression: ExpressionDeclaration, arguments: List<ExpressionDeclaration>) =
        BinaryExpressionDeclaration(
            left = variableExpression,
            operator = "=",
            right = arguments.fold(
                initial = variableExpression
            ) { left, right ->
                BinaryExpressionDeclaration(
                    left = left,
                    operator = "+",
                    right = if (right is SpreadExpressionDeclaration) {
                        right.expression
                    } else {
                        right
                    }
                )
            }
        )

    private fun transformCall(
        declaration: CallExpressionDeclaration,
        expression: ExpressionDeclaration,
        variableExpression: ExpressionDeclaration,
        methodName: String,
        variableName: NameEntity? = null
    ) : ExpressionDeclaration {

        // TODO: actually determine if left-side of a call is Set, Map or Array
        val isSet = true
        val isMap = true
        val isArray = true

        if (isSet) {
            for ((old, new) in setMethodRenames) {
                if (methodName == old) {
                    return declaration.copy(
                        expression = when (expression) {
                            is QualifierExpressionDeclaration -> expression.copy(
                                qualifier = variableName!!.appendLeft(IdentifierEntity(new))
                            )
                            is PropertyAccessExpressionDeclaration -> expression.copy(
                                name = IdentifierEntity(new)
                            )
                            else -> expression
                        }
                    )
                }
            }
        }

        if (isMap) {
            for ((old, new) in mapMethodRenames) {
                if (methodName == old) {
                    return declaration.copy(
                        expression = when (expression) {
                            is QualifierExpressionDeclaration -> expression.copy(
                                qualifier = variableName!!.appendLeft(IdentifierEntity(new))
                            )
                            is PropertyAccessExpressionDeclaration -> expression.copy(
                                name = IdentifierEntity(new)
                            )
                            else -> expression
                        }
                    )
                }
            }
        }

        if (isArray && methodName == "push") {
            return transformPush(variableExpression, declaration.arguments)
        }

        return declaration
    }

    private fun lowerCallExpressionDeclaration(declaration: CallExpressionDeclaration): ExpressionDeclaration {

        val expression = declaration.expression
        if (expression is QualifierExpressionDeclaration) {

            val variableName = expression.qualifier.left
            val variableExpression = if (variableName is IdentifierEntity) {
                IdentifierExpressionDeclaration(variableName)
            } else {
                QualifierExpressionDeclaration(variableName as QualifierEntity)
            }
            val methodName = expression.qualifier.right.value

            return transformCall(declaration, expression, variableExpression, methodName, variableName)
        }
        if (expression is PropertyAccessExpressionDeclaration) {
            val variableExpression = expression.expression
            val methodName = expression.name.value

            return transformCall(declaration, expression, variableExpression, methodName)
        }
        return declaration
    }

    override fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        return when (val newDeclaration = super.lower(declaration)) {
            is NewExpressionDeclaration -> lowerNewExpressionDeclaration(newDeclaration)
            is CallExpressionDeclaration -> lowerCallExpressionDeclaration(newDeclaration)
            else -> newDeclaration
        }
    }
}

class ResolveCollections : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map {
            it.copy(root = CollectionsLowering().lowerSourceDeclaration(it.root))
        })
    }
}