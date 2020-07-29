package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration

internal class ReplaceExpressionsLowering(private val replacements: Map<ExpressionDeclaration, IdentifierExpressionDeclaration>):
    DeclarationLowering {
    override fun lowerIdentifier(identifier: IdentifierEntity): IdentifierEntity {
        return replacements[IdentifierExpressionDeclaration(
            identifier
        )]?.identifier ?: identifier
    }

    override fun lowerQualifier(qualifier: QualifierEntity): NameEntity {
        return replacements[QualifierExpressionDeclaration(
            qualifier
        )]?.identifier ?: super.lowerQualifier(qualifier)
    }

    override fun lower(declaration: ExpressionDeclaration): ExpressionDeclaration {
        return when (declaration) {
            is PropertyAccessExpressionDeclaration -> {
                return replacements[declaration] ?: super.lower(declaration)
            }
            else -> super.lower(declaration)
        }
    }
}