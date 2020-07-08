package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ArrayDestructuringDeclaration
import org.jetbrains.dukat.tsmodel.BindingVariableDeclaration
import org.jetbrains.dukat.tsmodel.ForOfStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

class ProcessForOfStatementsLowering : DeclarationLowering {
    override fun lowerForOfStatementDeclaration(statement: ForOfStatementDeclaration): ForOfStatementDeclaration {
        return when (statement.variable) {
            is ArrayDestructuringDeclaration -> {
                val fictiveVariable = VariableDeclaration(
                    name = "_i",
                    type = TypeDeclaration(
                        value = IdentifierEntity("Any"),
                        params = listOf()
                    ),
                    modifiers = setOf(),
                    initializer = null,
                    definitionsInfo = listOf(),
                    uid = "",
                    explicitlyDeclaredType = false
                )
                val newAssignments =
                    (statement.variable as ArrayDestructuringDeclaration).elements.mapIndexedNotNull { index, element ->
                        if (index < 2 && element is BindingVariableDeclaration) {
                            VariableDeclaration(
                                name = element.name,
                                type = TypeDeclaration(
                                    value = IdentifierEntity("Any"),
                                    params = listOf()
                                ),
                                modifiers = setOf(),
                                initializer = QualifierExpressionDeclaration(
                                    QualifierEntity(
                                        IdentifierEntity(fictiveVariable.name),
                                        IdentifierEntity(when (index) {
                                            0 -> "key"
                                            1 -> "value"
                                            else -> raiseConcern("invalid index in $element") { "" }
                                        })
                                    )
                                ),
                                definitionsInfo = listOf(),
                                uid = "",
                                explicitlyDeclaredType = false
                            )
                        } else {
                            // TODO: nested destructuring
                            // TODO: not only key/value destructuring, but also normal arrays
                            null
                        }
                    }
                statement.copy(
                    variable = fictiveVariable,
                    body = statement.body.copy(
                        statements = newAssignments + statement.body.statements
                    )
                )
            }
            else -> statement
        }
    }
}

class ProcessForOfStatements : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFileDeclaration ->
            sourceFileDeclaration.copy(
                root = ProcessForOfStatementsLowering().lowerSourceDeclaration(
                    sourceFileDeclaration.root
                )
            )
        })
    }
}