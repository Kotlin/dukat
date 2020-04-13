package org.jetbrains.dukat.tsmodel.expression.templates

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ExpressionTemplateTokenDeclaration(
    val expression: ExpressionDeclaration
) : TemplateTokenDeclaration