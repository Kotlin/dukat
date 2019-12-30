package org.jetbrains.dukat.tsmodel.expression.literal

import org.jetbrains.dukat.tsmodel.MemberDeclaration

data class ObjectLiteralExpressionDeclaration(
        val members: List<MemberDeclaration>
) : LiteralExpressionDeclaration