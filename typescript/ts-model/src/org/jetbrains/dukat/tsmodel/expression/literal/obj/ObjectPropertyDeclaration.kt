package org.jetbrains.dukat.tsmodel.expression.literal.obj

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class ObjectPropertyDeclaration(
        val name: String,
        val initializer: ExpressionDeclaration?
) : ObjectMemberDeclaration