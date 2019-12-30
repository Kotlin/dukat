package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

class ElementAccessExpressionDeclaration(
        val expression: ExpressionDeclaration,
        val argumentExpression: ExpressionDeclaration
) : ExpressionDeclaration