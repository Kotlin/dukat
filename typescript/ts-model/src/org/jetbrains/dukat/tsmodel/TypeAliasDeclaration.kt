package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeAliasDeclaration(
        val aliasName: NameEntity,
        val typeParameters: List<IdentifierEntity>,
        val typeReference: ParameterValueDeclaration,

        val uid: String
): TopLevelDeclaration