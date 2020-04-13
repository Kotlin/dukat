package org.jetbrains.dukat.tsmodel.expression.templates

import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration

data class StringTemplateTokenDeclaration(
    val value: StringLiteralExpressionDeclaration
) : TemplateTokenDeclaration