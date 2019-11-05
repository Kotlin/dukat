package org.jetbrains.dukat.tsmodel.expression.name

import org.jetbrains.dukat.astCommon.QualifierEntity

data class QualifierExpressionDeclaration(
        val qualifier: QualifierEntity
) : NameExpressionDeclaration