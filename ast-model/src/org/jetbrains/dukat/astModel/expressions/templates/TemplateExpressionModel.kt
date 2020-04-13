package org.jetbrains.dukat.astModel.expressions.templates

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class TemplateExpressionModel(
    val tokens: List<TemplateTokenModel>
): ExpressionModel
