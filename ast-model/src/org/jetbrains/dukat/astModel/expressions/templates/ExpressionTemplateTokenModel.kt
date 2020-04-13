package org.jetbrains.dukat.astModel.expressions.templates

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class ExpressionTemplateTokenModel(
    val expression: ExpressionModel
) : TemplateTokenModel