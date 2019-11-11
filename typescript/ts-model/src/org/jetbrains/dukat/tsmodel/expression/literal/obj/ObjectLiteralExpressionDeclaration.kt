package org.jetbrains.dukat.tsmodel.expression.literal.obj

import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration

data class ObjectLiteralExpressionDeclaration(
        val members: List<ObjectMemberDeclaration>
) : LiteralExpressionDeclaration