package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration

/*fun ExpressionDeclaration.convert(): ExpressionModel? {
    return null
}

fun ExpressionStatementDeclaration.convert(): StatementModel? {
    return expression.convert()
}*/

fun StatementDeclaration.convert(): StatementModel? {
    return when (this) {
        is ExpressionStatementDeclaration -> convert()
        else -> null
    }
}

fun BlockDeclaration.convert(): List<StatementModel> {
    return statements.mapNotNull { it.convert() }
}