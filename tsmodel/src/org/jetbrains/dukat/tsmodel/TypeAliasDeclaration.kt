package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstTopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeAliasDeclaration(
        val aliasName: QualifiedLeftDeclaration,
        val typeParameters: List<IdentifierDeclaration>,
        val typeReference: ParameterValueDeclaration,

        val uid: String
): AstTopLevelEntity