package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astCommon.IdentifierEntity

data class DefinedExternallyExpressionModel(val previousExpression: ExpressionModel?): ExpressionModel {
   val identifier = IdentifierEntity("definedExternally")
}