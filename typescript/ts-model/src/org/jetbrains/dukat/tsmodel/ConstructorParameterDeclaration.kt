package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

interface ConstructorParameterDeclaration : Entity {
    val name: String
    val type: ParameterValueDeclaration
    val initializer: ExpressionDeclaration?
}