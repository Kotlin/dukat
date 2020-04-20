package org.jetbrains.dukat.astModel.expressions.templates

import org.jetbrains.dukat.astModel.expressions.literals.StringLiteralExpressionModel

data class StringTemplateTokenModel(
    val value: StringLiteralExpressionModel
) : TemplateTokenModel