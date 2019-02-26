package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeAliasDeclaration(
        val aliasName: String,
        val typeParameters: List<IdentifierDeclaration>,
        val typeReference: ParameterValueDeclaration
): TopLevelDeclaration


fun TypeAliasDeclaration.getUID(): String {
    return aliasName + "_TYPE"
}