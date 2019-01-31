package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


data class ExpressionDeclaration(
        val kind: TypeDeclaration,
        val meta: String?
) : Declaration